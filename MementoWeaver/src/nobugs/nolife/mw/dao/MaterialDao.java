package nobugs.nolife.mw.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.util.PersistenceUtil;

public class MaterialDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	
	public int remove(Material m) {
		Query query = em.createQuery("DELETE FROM Material m WHERE m.materialId = :materialId");
		query.setParameter("materialId", m.getMaterialId());
		
		em.getTransaction().begin();
		int rowAffected = query.executeUpdate();
		em.getTransaction().commit();
		return rowAffected;

	}

}
