package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.TagConfig;
import nobugs.nolife.mw.util.PersistenceUtil;

public class TagConfigDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	/**
	 * ���O��`�ς݂�Tag�ꗗ��ԋp����.
	 * @return ���O��`�ς݃^�O(String)�̃��X�g
	 */
	public List<String> findTagList() {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.tag FROM TagConfig tagConfig",String.class);
		return query.getResultList();
	}
	
	/**
	 * �V�X�e���ŉi�����Ǘ�����Ă���S�Ẵ^�O�ݒ���擾����.
	 * @return �V�X�e���ŉi�����Ǘ�����Ă���^�O�ݒ�̃��X�g
	 */
	public List<TagConfig> findAll() {
		TypedQuery<TagConfig> query = em.createQuery(
				"SELECT tagConfig FROM TagConfig tagConfig",TagConfig.class);
		return query.getResultList();
	}

	/**
	 * �w�肳�ꂽTag�̃^�O�ݒ���擾����.
	 * @return �V�X�e���ŉi�����Ǘ�����Ă���^�O�ݒ�
	 */
	public TagConfig find(String tag) {
		return em.find(TagConfig.class, tag);
	}

	/**
	 * �w�肳�ꂽ�W�F�l���[�^���ɑΉ�����T�u�W�F�l���[�^FQCN��ԋp����.
	 * @param generatorName
	 * @return �w�肳�ꂽ�W�F�l���[�^�̃T�u�W�F�l���[�^FQCN. �Y���Ȃ��̏ꍇ��Null��ԋp
	 */
	public String findSubGeneratorFQCNByGeneratorName(String generatorName) {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.subGeneratorFqcn FROM TagConfig tagConfig WHERE tagConfig.generatorName = :generatorName",String.class);
		query.setParameter("generatorName", generatorName);
		List<String> resultList = query.getResultList();
		if(resultList.isEmpty() || resultList.size()==0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	/**
	 * �w�肳�ꂽ�J�e�S���ɑΉ������W�F�l���[�^����ԋp����.
	 * @param category
	 * @return �w�肳�ꂽ�J�e�S���̃W�F�l���[�^��. �Y���Ȃ��̏ꍇ��Null��ԋp
	 */
	public String findGeneratorNameByCategory(String category) {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.generatorName FROM TagConfig tagConfig WHERE tagConfig.category = :category",String.class);
		query.setParameter("category", category);
		List<String> resultList = query.getResultList();
		if(resultList.isEmpty() || resultList.size()==0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

}
