package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
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

	/** TaggedMaterial tm�Ɠ��ꃁ�����g�ɑ�����TaggedMaterial���������邽�߂�TypedQuery��ԋp���� */
	protected abstract TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(TaggedMaterial tm);
	
	/** �������g�𐶐����ԋp���� 
	 * @throws MWException */
	protected abstract Memento generateMemento(List<TaggedMaterial> updateTargetList);


	/**
	 * �n���ꂽMaterial���܂ރ������g�𐶐����ԋp����.
	 * �e���v���[�g���\�b�h�Ƃ��ăT�u�N���X�Ŏ�������钊�ۃ��\�b�h���Ă�ł���.
	 * @param m �X�e�[�W���O��Ԃ̃^�O�t�f��(TaggedMaterial)��������f��(Material)
	 * @return �쐬�E�X�V�����TaggedMaterial���Z�b�g�ς݂�Memento
	 * @throws MWException 
	 */
	public Memento generate(TaggedMaterial tm){
		logger.info("�f��["+tm.getMaterial().getMaterialId()+"]�������郁�����g�̍쐬���J�n���܂�");

		TypedQuery<TaggedMaterial> query = queryBelongingSameMementoWith(tm);
		List<TaggedMaterial> updateTargetList = query.getResultList();
		logger.info("�����������g�̑f�ނ�"+updateTargetList.size()+"���ł�");

		Memento memento = generateMemento(updateTargetList);
		generatedMementoList.add(memento.getProductionPath());
		logger.info("�������g["+memento+"]����������܂���");
		
		return memento;
	}
	
	
	/**
	 * �����Ώۃ������g�𔻒肵�A�����������g���X�g�Ƃ��ĕۊǂ���B
	 * �e���v���[�g���\�b�h�Ƃ��ăT�u�N���X�Ŏ�������钊�ۃ��\�b�h���Ă�ł���B
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

	/* �e���v���[�g����g�p���邽�߂̃��\�b�h */
	public String yyyymmdd(Material m) { return MaterialUtil.getMaterialYearMonthDate(m); }
	public String yyyymm(Material m) { return MaterialUtil.getMaterialYearMonth(m); }
	public String year(Material m) { return MaterialUtil.getMaterialYear(m); }
	public String month(Material m) { return MaterialUtil.getMaterialMonth(m); }
	public String date(Material m) { return MaterialUtil.getMaterialDate(m); }
	public String hour(Material m) { return MaterialUtil.getMaterialHour(m); }
	public String minute(Material m) { return MaterialUtil.getMaterialMinute(m); }
	public String second(Material m) { return MaterialUtil.getMaterialSecond(m); }
	
	public String fileName(Material m) {return PathUtil.getFileName(m); }
	public String photoFileName(Material m) {return PathUtil.getPhotoFileName(m); }
}
