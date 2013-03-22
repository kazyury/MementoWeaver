package nobugs.nolife.mw.processing;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.util.PersistenceUtil;

public class UpdateTagProcessor {

	public void updateTagProcess(Material m){
		EntityManager em = PersistenceUtil.getMWEntityManager();
		em.getTransaction().begin();
		em.merge(m);
		em.getTransaction().commit();
		em.close();
	}
}
