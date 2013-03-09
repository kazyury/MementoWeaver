package nobugs.nolife.mw.util;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PersistenceUtil {
	public static EntityManager getMWEntityManager() {
		return Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();
	}

}
