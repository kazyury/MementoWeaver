package nobugs.nolife.mw.util;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;

public class PersistenceUtil {
	private static Logger logger = Logger.getGlobal();
	
	public static EntityManager getMWEntityManager() {
		return Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();
	}
	
	public static void logPersistenceState(Material m, EntityManager em) {
		int id = System.identityHashCode(m);
		boolean isManaged = em.contains(m);
		logger.info("Material["+m.getMaterialId()+"]	id["+id+"]	isManaged["+isManaged+"]");

		for(TaggedMaterial tm:m.getTaggedMaterials()){
			logPersistenceState(tm, em);
		}
	}

	public static void logPersistenceState(Memento m, EntityManager em) {
		int id = System.identityHashCode(m);
		boolean isManaged = em.contains(m);
		logger.info("Memento["+m.getMementoId()+"]	id["+id+"]	isManaged["+isManaged+"]");

		for(TaggedMaterial tm:m.getTaggedMaterials()){
			logPersistenceState(tm, em);
		}
	}

	public static void logPersistenceState(TaggedMaterial tm, EntityManager em) {
		int id = System.identityHashCode(tm);
		boolean isManaged = em.contains(tm);
		String materialId = tm.getId().getMaterialId();
		String tag = tm.getId().getTag();
		logger.info("	TaggedMaterial["+materialId+", "+tag+"]	id["+id+"]	isManaged["+isManaged+"]");
	}


}
