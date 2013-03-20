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

	/** Material m�Ɠ��ꃁ�����g�ɑ�����TaggedMaterial���������邽�߂�TypedQuery��ԋp���� */
	protected abstract TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(Material m);
	
	/** �������g�𐶐����� */
	protected abstract String generateMemento(List<TaggedMaterial> updateTargetList);

	// template method
	/**
	 * �n���ꂽMaterial���܂ރ������g�𐶐����A���̃������g�Ɋ܂܂��TaggedMaterial��ԋp����
	 * @param m
	 * @return
	 */
	public List<TaggedMaterial> generate(Material m){
		logger.info("�f��["+m.getMaterialId()+"]�������郁�����g�̍쐬���J�n���܂�");

		TypedQuery<TaggedMaterial> query = queryBelongingSameMementoWith(m);
		List<TaggedMaterial> updateTargetList = query.getResultList();
		logger.info("�����������g�̑f�ނ�"+updateTargetList.size()+"���ł�");

		String memento = generateMemento(updateTargetList);
		generatedMementoList.add(memento);
		logger.info("�������g["+memento+"]����������܂���");
		
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
