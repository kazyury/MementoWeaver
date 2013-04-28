package nobugs.nolife.mw.generator;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.util.PersistenceUtil;

public abstract class SubGenerator {
	protected static Logger logger = Logger.getGlobal();
	protected EntityManager em = PersistenceUtil.getMWEntityManager();
	
	protected abstract void generateSubMemento();

	public void generate(){
		generateSubMemento();
	}
}
