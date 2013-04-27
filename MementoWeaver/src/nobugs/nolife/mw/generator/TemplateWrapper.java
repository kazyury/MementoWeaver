package nobugs.nolife.mw.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import nobugs.nolife.mw.MWException;

public class TemplateWrapper {
	private static Logger logger = Logger.getGlobal();

	/** テンプレート上の指示子とオブジェクトのマップ(必須) */
	private Map<String, Object> context = null;

	/** テンプレートファイルのパス(必須) */
	private String template = null;

	/** URL上の階層 */
	private int level = -1;

	/** 出力ファイル名(outメソッドには必須) */
	private String output = null;

	/** 適用結果(set不可) */
	private String contents = null;

	public TemplateWrapper(){ }

	// setter/getter
	public void setTemplate(String template) { this.template = template; }
	public void setContext(Map<String,Object> context) { this.context = context; }
	public void setLevel(int level) { this.level = level; }
	public void setOutput(String output) { this.output = output; }

	public String getTemplate() { return this.template; }
	public Map<String,Object> getContext() { return this.context; }
	public int getLevel() { return this.level; }
	public String getOutput() { return this.output; }

	/**
	 * テンプレート適用結果を文字列の形式で返却する.
	 *
	 * @return テンプレート適用結果の文字列表現
	 * @throws MWException 下位で発生した例外はMWException に変換してthrow する。
	 */
	public String getContents() throws MWException {
		if(this.contents == null) {
			logger.fine("this.contents が null のため、apply()を実行します。");
			return apply();
		} else {
			logger.fine("this.contentsを返却します。");
			return this.contents;
		}
	}
	/**
	 * テンプレート適用結果を指定されたファイルに出力する.
	 * ファイル名がsetOutputで指定されていない場合には、例外をthrowする
	 *
	 * Unit test is available.
	 *
	 * @throws MWException 下位で発生した例外はMWException に変換してthrow する。
	 */
	public void out() throws MWException {
		if(this.output == null) { throw new MWException("出力ファイル名が指定されていません。"); }
		if(this.contents == null) { apply(); }

		// 書き込みできないパス(親ディレクトリが存在しない,不正なファイル名等)の場合には例外送出
		File outfile = new File(this.output);
		File parent = outfile.getParentFile();
		if (parent==null || !parent.canWrite()) { 
			throw new MWException("書き込みできない出力ファイル名です。["+this.output+"]"); 
		}

		try {
			FileWriter fw = new FileWriter(outfile);
			fw.write(this.contents);
			fw.close();
		} catch(IOException e) {
			throw new MWException(e.getMessage());
		}
	}

	/**
	 * テンプレートを適用し、結果を記憶する.
	 * テンプレートが設定されていない場合、コンテキストのキーに"this"が存在する場合には例外を送出する。
	 *
	 * Unit test is available.
	 *
	 * @return テンプレート適用結果文字列
	 * @throws MWException 下位で発生した例外はMWException に変換してthrow する。
	 */
	public String apply() throws MWException {
		logger.info("テンプレート適用を開始します。");
		if (this.template == null) {
			throw new MWException("テンプレートが指定されていません。setTemplate()を使用してください。");
		}

		if (this.context == null) {
			logger.warning("コンテキストが未設定のため、デフォルト値で処理を開始します。");
			this.context = new HashMap<String, Object>();
		}

		if (this.context.containsKey("this")) {
			throw new MWException("コンテキストのキーにthisキーワードは使用できません。");
		}

		this.context.put("this",this);

		logger.info("context  : ["+context+"]");
		logger.info("template : ["+template+"]");
		logger.info("level    : ["+level+"]");
		logger.info("output   : ["+output+"]");
		logger.info("contents : ["+contents+"]");
		return _apply(this.template, this.context);
	}


	/**
	 * テンプレートを適用し、結果を記憶する.
	 *
	 * Unit test is available.
	 *
	 * @return テンプレート適用結果文字列
	 * @throws MWException 下位で発生した例外はMWException に変換してthrow する。
	 */
	private String _apply(String template, Map<String, Object> context) throws MWException {
		// FIXME velocity のプロパティファイルの読み出しが一寸微妙なようなので、ハードコードしている。
		logger.fine("_apply:Velocityプロパティの設定開始");
		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("input.encoding", "Shift_JIS");
		p.setProperty("output.encoding", "Shift_JIS");
		StringWriter writer = new StringWriter();

		try {
			logger.fine("_apply:Velocity.init");
			Velocity.init(p);
			VelocityContext vc = new VelocityContext();

			logger.fine("_apply:context setting.");
			Set<String> set = context.keySet();
			for(String key : set) {
				logger.fine("_apply:context setting.["+key+"],["+context.get(key)+"]");
				vc.put(key, context.get(key));
			}
			logger.fine("_apply:context setting end.");

			logger.fine("_apply:getTemplate("+template+").");
			Template vt = Velocity.getTemplate(template);
			logger.fine("_apply:getTemplate. vt was ["+vt+"].");

			if (vt != null) {
				vt.merge(vc, writer);
			} else {
				logger.warning("template is null!");
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
			throw new MWException(e.getMessage());
		}

		this.contents = writer.toString();
		return this.contents;
	}

	/**
	 * HTMLコンテンツを生成する際のURL階層上のレベルから../の適切な繰り返しを返却.
	 * テンプレートからのCallback用
	 * setLevelでレベルが指定されていない場合には、警告のうえで空文字が返却される。
	 *
	 * Unit test is available.
	 *
	 * @return 適切な回数分繰り返された "../"
	 */
	public String predecessor() {
		if ( this.level < 0 ){
			logger.warning("setLevelが適切に実施されていません。レベル0で処理を継続します。");
			this.level = 0;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < this.level ; i++ ){
			buf.append("../");
		}
		return buf.toString();
	}
}