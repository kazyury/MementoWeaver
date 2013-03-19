package nobugs.nolife.mw.generator;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.persistence.PredefinedTag;
import nobugs.nolife.mw.util.PersistenceUtil;

public class GeneratorFactory {
	private static Logger logger = Logger.getGlobal();
	
	public static Generator getGenerator(String tag) throws MWException{
		EntityManager em = PersistenceUtil.getMWEntityManager();
		PredefinedTag pt = em.find(PredefinedTag.class, tag);
		em.close();
		
		logger.info("タグ["+tag+"]に関連付けられたFQCNは ["+pt.getFqcn()+"]です。");

		Generator generator = null;
		Class<?> klass;
		try {
			klass = Class.forName(pt.getFqcn());
			generator = (Generator) klass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new MWException("例外が発生しました",e.getCause());
		}
		return generator;
	}
}
