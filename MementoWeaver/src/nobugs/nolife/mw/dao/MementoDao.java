package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	
	/**
	 * 指定されたメメントを削除する
	 * @param memento
	 * @return 削除した件数
	 */
	public int remove(Memento memento) {
		Query query = em.createQuery("DELETE FROM Memento memento WHERE memento.mementoId = :mementoId");
		query.setParameter("mementoId", memento.getMementoId());
		
		em.getTransaction().begin();
		int rowAffected = query.executeUpdate();
		em.getTransaction().commit();
		return rowAffected;
	}

	/**
	 * メメントカテゴリのListを返却する
	 * @return 文字列(String)で表現されたDB登録済みのメメントカテゴリのリスト
	 */
	public List<String> findCategory() {
		TypedQuery<String> query = em.createQuery(
				"SELECT DISTINCT me.category FROM Memento me order by me.category",String.class);
		return query.getResultList();
	}

	/**
	 * システムで永続化管理されているメメントを取得する.
	 * @return システムで永続化管理されているmementoのリスト
	 */
	public List<Memento> findAll() {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me order by me.category, me.mementoId",Memento.class);
		return query.getResultList();
	}
	
	/**
	 * システムで永続化管理されているメメントをカテゴリを指定して取得する.
	 * @return システムで永続化管理されているmementoのリスト
	 */
	public List<Memento> findByCategory(String category) {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me WHERE me.category = :category order by me.category",Memento.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
	
	/**
	 * ファイルパスを指定してシステムで永続化管理されているメメントを取得する
	 * @param path メメントのファイルパス
	 * @return 指定されたファイルパスのMemento.存在しない場合Nullを返却
	 */
	public Memento findByPath(String path) {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT m FROM Memento m WHERE m.productionPath = :path", Memento.class);
		query.setParameter("path", path);
		List<Memento> result = query.getResultList();
		if(result.isEmpty()){
			return null;
		} else {
			return result.get(0);
		}
	}
}
