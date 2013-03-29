package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.Map;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;


public class ChronicleIndexGenerator extends SubGenerator {

	@Override
	protected void generateSubMemento() throws MWException {
		String indexfilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_CHRONICLE)+"\\index.html";
		String familyFilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_CHRONICLE)+"\\family.xml";

		// index.html�̍쐬
		generateIndexPage(indexfilePath);

		// family.xml�̍쐬
		generateFamilyXml(familyFilePath);

	}

	/**
	 * �ڎ����쐬����
	 * @param indexfilePath
	 * @throws MWException
	 */
	private void generateIndexPage(String indexfilePath) throws MWException {

		// velocity�p�̃}�b�v
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title","�N���j�N��");

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/chronicleIndex.html");
		tw.setLevel(1);
		tw.setOutput(indexfilePath);
		tw.out();
	}

	private void generateFamilyXml(String familyFilePath) throws MWException {
		// velocity �p��map
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title","�N���j�N��");

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/chronicleFamily.xml");
		tw.setLevel(1);
		tw.setOutput(familyFilePath);
		tw.out();
	}

}
