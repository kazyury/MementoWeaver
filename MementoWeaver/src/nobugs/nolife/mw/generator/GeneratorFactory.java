package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.PredefinedTag;
import nobugs.nolife.mw.util.PersistenceUtil;

public class GeneratorFactory {
	private static Logger logger = Logger.getGlobal();
	private static Map<String, Generator> generatorMap = new HashMap<String, Generator>();
	private static Map<String, String> subGeneratorMap = new HashMap<String, String>();

	static {
		subGeneratorMap.put("AlbumGenerator","nobugs.nolife.mw.generator.AlbumIndexGenerator");
		// TODO �ȉ��͂Ƃ肠�����_�~�[
		subGeneratorMap.put("PartyGenerator","nobugs.nolife.mw.generator.AlbumIndexGenerator");
		subGeneratorMap.put("TreasureGenerator","nobugs.nolife.mw.generator.AlbumIndexGenerator");
		subGeneratorMap.put("WinnerGenerator","nobugs.nolife.mw.generator.AlbumIndexGenerator");
		subGeneratorMap.put("ChronicleGenerator","nobugs.nolife.mw.generator.AlbumIndexGenerator");

	}

	public static Generator getGenerator(String tag) throws MWException{
		EntityManager em = PersistenceUtil.getMWEntityManager();
		PredefinedTag pt = em.find(PredefinedTag.class, tag);
		em.close();
		
		logger.info("�^�O["+tag+"]�Ɋ֘A�t����ꂽFQCN�� ["+pt.getFqcn()+"]�ł��B");
		// TODO PredefinedTag�ɖ��o�^�̃^�O����͂�����NullPointer�ɂȂ�B

		Generator generator = generatorMap.get(pt.getFqcn());
		if (generator != null) {
			return generator;
		} else {
			Class<?> klass;
			try {
				klass = Class.forName(pt.getFqcn());
				generator = (Generator) klass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new MWException("��O���������܂���",e.getCause());
			}
			generatorMap.put(pt.getFqcn(), generator);
			return generator;
		}
	}

	public static SubGenerator getSubGenerator(String mainGeneratorName) throws MWException {
		Class<?> klass;
		SubGenerator subGenerator;
		try {
			klass = Class.forName(subGeneratorMap.get(mainGeneratorName));
			subGenerator = (SubGenerator) klass.newInstance();
		} catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			throw new MWException("��O���������܂���",e.getCause());
		}
		return subGenerator;
	}


}
