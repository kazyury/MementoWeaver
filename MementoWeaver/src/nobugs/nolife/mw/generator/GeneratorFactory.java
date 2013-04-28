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
		
		logger.info("タグ["+tag+"]に関連付けられたFQCNは ["+tagConfig.getGeneratorFqcn()+"]です。");
		// TODO PredefinedTagに未登録のタグを入力したらNullPointerになる。

		Generator generator = generatorMap.get(tagConfig.getGeneratorFqcn());
		if (generator != null) {
			return generator;
		} else {
			Class<?> klass;
			try {
				klass = Class.forName(tagConfig.getGeneratorFqcn());
				generator = (Generator) klass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new MWConfigurationError("例外が発生しました",e.getCause());
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
			throw new MWConfigurationError("例外が発生しました",e.getCause());
		}
		return subGenerator;
	}
}
