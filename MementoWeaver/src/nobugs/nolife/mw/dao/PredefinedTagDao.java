package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.util.PersistenceUtil;

public class PredefinedTagDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	/**
	 * 事前定義済みのTag一覧を返却する.
	 * @return 事前定義済みタグ(String)のリスト
	 */
	public List<String> findAll() {
		TypedQuery<String> query = em.createQuery(
				"SELECT pt.tag FROM PredefinedTag pt",String.class);
		return query.getResultList();
	}

}
