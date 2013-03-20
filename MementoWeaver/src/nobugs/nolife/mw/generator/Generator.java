package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.PersistenceUtil;

public abstract class Generator {
	protected static Logger logger = Logger.getGlobal();
	protected List<String> preparedMementoList = new ArrayList<String>();
	protected List<String> generatedMementoList = new ArrayList<String>();
	protected EntityManager em = PersistenceUtil.getMWEntityManager();

	protected abstract String affectedMemento(Material m, String tag);

	/** Material mと同一メメントに属するTaggedMaterialを検索するためのTypedQueryを返却する */
	protected abstract TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(Material m);
	
	/** メメントを生成する */
	protected abstract String generateMemento(List<TaggedMaterial> updateTargetList);

	// template method
	/**
	 * 渡されたMaterialを含むメメントを生成し、そのメメントに含まれるTaggedMaterialを返却する
	 * @param m
	 * @return
	 */
	public List<TaggedMaterial> generate(Material m){
		logger.info("素材["+m.getMaterialId()+"]が属するメメントの作成を開始します");

		TypedQuery<TaggedMaterial> query = queryBelongingSameMementoWith(m);
		List<TaggedMaterial> updateTargetList = query.getResultList();
		logger.info("所属メメントの素材は"+updateTargetList.size()+"件です");

		String memento = generateMemento(updateTargetList);
		generatedMementoList.add(memento);
		logger.info("メメント["+memento+"]が生成されました");
		
		return updateTargetList;
	}
	
	// template method
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
}
