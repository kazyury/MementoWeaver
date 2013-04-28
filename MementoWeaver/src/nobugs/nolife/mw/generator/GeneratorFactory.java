package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nobugs.nolife.mw.dao.TagConfigDao;
import nobugs.nolife.mw.entities.TagConfig;
import nobugs.nolife.mw.exceptions.MWConfigurationError;

public class GeneratorFactory {
	private static Logger logger = Logger.getGlobal();
	private static Map<String, Generator> generatorMap = new HashMap<String, Generator>();
	private static TagConfigDao tagConfigDao = new TagConfigDao();

	public static Generator getGenerator(String tag){
		TagConfig tagConfig = tagConfigDao.find(tag);
		
		logger.info("�^�O["+tag+"]�Ɋ֘A�t����ꂽFQCN�� ["+tagConfig.getGeneratorFqcn()+"]�ł��B");
		// TODO PredefinedTag�ɖ��o�^�̃^�O����͂�����NullPointer�ɂȂ�B

		Generator generator = generatorMap.get(tagConfig.getGeneratorFqcn());
		if (generator != null) {
			return generator;
		} else {
			Class<?> klass;
			try {
				klass = Class.forName(tagConfig.getGeneratorFqcn());
				generator = (Generator) klass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new MWConfigurationError("��O���������܂���",e.getCause());
			}
			generatorMap.put(tagConfig.getGeneratorFqcn(), generator);
			return generator;
		}
	}

	public static SubGenerator getSubGenerator(String mainGeneratorName) {
		String subGeneratorFqcn = tagConfigDao.findSubGeneratorFQCNByGeneratorName(mainGeneratorName);
		Class<?> klass;
		SubGenerator subGenerator;
		try {
			klass = Class.forName(subGeneratorFqcn);
			subGenerator = (SubGenerator) klass.newInstance();
		} catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			throw new MWConfigurationError("��O���������܂���",e.getCause());
		}
		return subGenerator;
	}
}
