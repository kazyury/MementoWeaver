package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.util.PersistenceUtil;

public class PredefinedTagDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	/**
	 * ���O��`�ς݂�Tag�ꗗ��ԋp����.
	 * @return ���O��`�ς݃^�O(String)�̃��X�g
	 */
	public List<String> findAll() {
		TypedQuery<String> query = em.createQuery(
				"SELECT pt.tag FROM PredefinedTag pt",String.class);
		return query.getResultList();
	}

}
