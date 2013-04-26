package nobugs.nolife.mw.processing;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.util.PersistenceUtil;

public class FindProcessor {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	public static List<String> findMementoCategoryProcess() {
		TypedQuery<String> query = em.createQuery(
				"SELECT DISTINCT me.category FROM Memento me order by me.category",String.class);
		return query.getResultList();
	}

	public static List<Memento> findMementoProcess(String category) {
		TypedQuery<Memento> query = em.createQuery(
				"SELECT me FROM Memento me WHERE me.category = :category order by me.category",Memento.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
	
	public static List<String> findPredefinedTagProcess() {
		TypedQuery<String> query = em.createQuery(
				"SELECT pt.tag FROM PredefinedTag pt",String.class);
		return query.getResultList();
	}

}
