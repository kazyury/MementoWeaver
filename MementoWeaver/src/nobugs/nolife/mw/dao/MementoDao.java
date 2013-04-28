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
	 * �w�肳�ꂽ�������g���폜����
	 * @param memento
	 * @return �폜��������
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
	 * �������g�J�e�S����List��ԋp����
	 * @return ������(String)�ŕ\�����ꂽDB�o�^�ς݂̃������g�J�e�S���̃��X�g
	 */
	public List<String> findCategory() {
		TypedQuery<String> query = em.createQuery(
				"SELECT DISTINCT me.category FROM Memento me order by me.category",String.class);
		return query.getResultList();
	}

	/**
	 * �V�X�e���ŉi�����Ǘ�����Ă��郁�����g���擾����.
	 * @return �V�X�e���ŉi�����Ǘ�����Ă���memento�̃��X�g
	 */
	public List<Memento> findAll() {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me order by me.category, me.mementoId",Memento.class);
		return query.getResultList();
	}
	
	/**
	 * �V�X�e���ŉi�����Ǘ�����Ă��郁�����g���J�e�S�����w�肵�Ď擾����.
	 * @return �V�X�e���ŉi�����Ǘ�����Ă���memento�̃��X�g
	 */
	public List<Memento> findByCategory(String category) {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me WHERE me.category = :category order by me.category",Memento.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
	
	/**
	 * �t�@�C���p�X���w�肵�ăV�X�e���ŉi�����Ǘ�����Ă��郁�����g���擾����
	 * @param path �������g�̃t�@�C���p�X
	 * @return �w�肳�ꂽ�t�@�C���p�X��Memento.���݂��Ȃ��ꍇNull��ԋp
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
