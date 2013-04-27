package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.PersistenceUtil;

public class TaggedMaterialDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	
	public List<TaggedMaterial> findByMaterialId(String materialId) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM TaggedMaterial tm WHERE tm.id.materialId = :materialId",TaggedMaterial.class);
		query.setParameter("materialId", materialId);
		return query.getResultList();
	}

	public int remove(TaggedMaterial tm) {
		Query query = em.createQuery("DELETE FROM TaggedMaterial tm WHERE tm.id.materialId = :materialId AND tm.id.tag = :tag");
		query.setParameter("materialId", tm.getId().getMaterialId());
		query.setParameter("tag", tm.getId().getTag());
		
		em.getTransaction().begin();
		int rowAffected = query.executeUpdate();
		em.getTransaction().commit();
		return rowAffected;

	}
}
