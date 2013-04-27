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

	/** �e���v���[�g��̎w���q�ƃI�u�W�F�N�g�̃}�b�v(�K�{) */
	private Map<String, Object> context = null;

	/** �e���v���[�g�t�@�C���̃p�X(�K�{) */
	private String template = null;

	/** URL��̊K�w */
	private int level = -1;

	/** �o�̓t�@�C����(out���\�b�h�ɂ͕K�{) */
	private String output = null;

	/** �K�p����(set�s��) */
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
	 * �e���v���[�g�K�p���ʂ𕶎���̌`���ŕԋp����.
	 *
	 * @return �e���v���[�g�K�p���ʂ̕�����\��
	 * @throws MWException ���ʂŔ���������O��MWException �ɕϊ�����throw ����B
	 */
	public String getContents() throws MWException {
		if(this.contents == null) {
			logger.fine("this.contents �� null �̂��߁Aapply()�����s���܂��B");
			return apply();
		} else {
			logger.fine("this.contents��ԋp���܂��B");
			return this.contents;
		}
	}
	/**
	 * �e���v���[�g�K�p���ʂ��w�肳�ꂽ�t�@�C���ɏo�͂���.
	 * �t�@�C������setOutput�Ŏw�肳��Ă��Ȃ��ꍇ�ɂ́A��O��throw����
	 *
	 * Unit test is available.
	 *
	 * @throws MWException ���ʂŔ���������O��MWException �ɕϊ�����throw ����B
	 */
	public void out() throws MWException {
		if(this.output == null) { throw new MWException("�o�̓t�@�C�������w�肳��Ă��܂���B"); }
		if(this.contents == null) { apply(); }

		// �������݂ł��Ȃ��p�X(�e�f�B���N�g�������݂��Ȃ�,�s���ȃt�@�C������)�̏ꍇ�ɂ͗�O���o
		File outfile = new File(this.output);
		File parent = outfile.getParentFile();
		if (parent==null || !parent.canWrite()) { 
			throw new MWException("�������݂ł��Ȃ��o�̓t�@�C�����ł��B["+this.output+"]"); 
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
	 * �e���v���[�g��K�p���A���ʂ��L������.
	 * �e���v���[�g���ݒ肳��Ă��Ȃ��ꍇ�A�R���e�L�X�g�̃L�[��"this"�����݂���ꍇ�ɂ͗�O�𑗏o����B
	 *
	 * Unit test is available.
	 *
	 * @return �e���v���[�g�K�p���ʕ�����
	 * @throws MWException ���ʂŔ���������O��MWException �ɕϊ�����throw ����B
	 */
	public String apply() throws MWException {
		logger.info("�e���v���[�g�K�p���J�n���܂��B");
		if (this.template == null) {
			throw new MWException("�e���v���[�g���w�肳��Ă��܂���BsetTemplate()���g�p���Ă��������B");
		}

		if (this.context == null) {
			logger.warning("�R���e�L�X�g�����ݒ�̂��߁A�f�t�H���g�l�ŏ������J�n���܂��B");
			this.context = new HashMap<String, Object>();
		}

		if (this.context.containsKey("this")) {
			throw new MWException("�R���e�L�X�g�̃L�[��this�L�[���[�h�͎g�p�ł��܂���B");
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
	 * �e���v���[�g��K�p���A���ʂ��L������.
	 *
	 * Unit test is available.
	 *
	 * @return �e���v���[�g�K�p���ʕ�����
	 * @throws MWException ���ʂŔ���������O��MWException �ɕϊ�����throw ����B
	 */
	private String _apply(String template, Map<String, Object> context) throws MWException {
		// FIXME velocity �̃v���p�e�B�t�@�C���̓ǂݏo�����ꐡ�����Ȃ悤�Ȃ̂ŁA�n�[�h�R�[�h���Ă���B
		logger.fine("_apply:Velocity�v���p�e�B�̐ݒ�J�n");
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
	 * HTML�R���e���c�𐶐�����ۂ�URL�K�w��̃��x������../�̓K�؂ȌJ��Ԃ���ԋp.
	 * �e���v���[�g�����Callback�p
	 * setLevel�Ń��x�����w�肳��Ă��Ȃ��ꍇ�ɂ́A�x���̂����ŋ󕶎����ԋp�����B
	 *
	 * Unit test is available.
	 *
	 * @return �K�؂ȉ񐔕��J��Ԃ��ꂽ "../"
	 */
	public String predecessor() {
		if ( this.level < 0 ){
			logger.warning("setLevel���K�؂Ɏ��{����Ă��܂���B���x��0�ŏ������p�����܂��B");
			this.level = 0;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < this.level ; i++ ){
			buf.append("../");
		}
		return buf.toString();
	}
}