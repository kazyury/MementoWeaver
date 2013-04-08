package nobugs.nolife.mw.processing;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.util.PersistenceUtil;

public class FindMementoProcessor {
	private static Logger logger = Logger.getGlobal();
	private EntityManager em = PersistenceUtil.getMWEntityManager();

	
	public List<String> findMementoCategoryProcess() {
		TypedQuery<String> query = em.createQuery(
				"SELECT DISTINCT me.category FROM Memento me order by me.category",String.class);
		return query.getResultList();
	}

	public List<Memento> findMementoProcess(String category) {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me WHERE me.category = :category order by me.category",Memento.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
}
