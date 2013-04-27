package nobugs.nolife.mw.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MementoDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	
	public int remove(Memento memento) {
		Query query = em.createQuery("DELETE FROM Memento memento WHERE memento.mementoId = :mementoId");
		query.setParameter("mementoId", memento.getMementoId());
		
		em.getTransaction().begin();
		int rowAffected = query.executeUpdate();
		em.getTransaction().commit();
		return rowAffected;

	}
}
