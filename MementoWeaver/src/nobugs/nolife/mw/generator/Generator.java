package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public abstract class Generator {
	protected static Logger logger = Logger.getGlobal();
	protected List<String> preparedMementoList = new ArrayList<String>();
	protected List<String> generatedMementoList = new ArrayList<String>();
	protected EntityManager em = PersistenceUtil.getMWEntityManager();

	
	protected abstract String affectedMemento(Material m, String tag);
	protected abstract String getMementoId(Material m, String tag);

	/** Material mと同一メメントに属するTaggedMaterialを検索するためのTypedQueryを返却する */
	protected abstract TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(Material m);
	
	/** メメントを生成し返却する 
	 * @throws MWException */
	protected abstract Memento generateMemento(List<TaggedMaterial> updateTargetList) throws MWException;


	/**
	 * 渡されたMaterialを含むメメントを生成し、そのメメントに含まれるTaggedMaterialを返却する.
	 * テンプレートメソッドとしてサブクラスで実装される抽象メソッドを呼んでいる.
	 * @param m ステージング状態のタグ付素材(TaggedMaterial)が属する素材(Material)
	 * @return
	 * @throws MWException 
	 */
	public Memento generate(Material m) throws MWException{
		logger.info("素材["+m.getMaterialId()+"]が属するメメントの作成を開始します");

		TypedQuery<TaggedMaterial> query = queryBelongingSameMementoWith(m);
		List<TaggedMaterial> updateTargetList = query.getResultList();
		logger.info("所属メメントの素材は"+updateTargetList.size()+"件です");

		Memento memento = generateMemento(updateTargetList);
		generatedMementoList.add(memento.getProductionPath());
		logger.info("メメント["+memento+"]が生成されました");
		
		return memento;
	}
	
	
	/**
	 * 生成対象メメントを判定し、準備メメントリストとして保管する。
	 * テンプレートメソッドとしてサブクラスで実装される抽象メソッドを呼んでいる。
	 * @param m
	 * @param tag
	 */
	public void prepare(Material m, String tag){
		String memento = affectedMemento(m, tag);
		if(!preparedMementoList.contains(memento) && memento!=null){
			preparedMementoList.add(affectedMemento(m, tag));
		}
	}

	public List<String> getGeneratedMemento() {
		return generatedMementoList;
	}

	public List<String> getPreparedMemento() {
		return preparedMementoList;
	}

	/* テンプレートから使用するためのメソッド */
	public String yyyymmdd(Material m) { return MaterialUtil.getMaterialYearMonthDate(m); }
	public String year(Material m) { return MaterialUtil.getMaterialYear(m); }
	public String month(Material m) { return MaterialUtil.getMaterialMonth(m); }
	public String date(Material m) { return MaterialUtil.getMaterialDate(m); }
	public String hour(Material m) { return MaterialUtil.getMaterialHour(m); }
	public String minute(Material m) { return MaterialUtil.getMaterialMinute(m); }
	public String second(Material m) { return MaterialUtil.getMaterialSecond(m); }
	
	public String fileName(Material m) throws MWException {return PathUtil.getFileName(m); }
	public String photoFileName(Material m) throws MWException {return PathUtil.getPhotoFileName(m); }
}
