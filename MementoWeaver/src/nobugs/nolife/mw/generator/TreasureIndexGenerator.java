package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.AgeCalculator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.StringUtil;


public class TreasureIndexGenerator extends SubGenerator {

	@Override
	protected void generateSubMemento() throws MWException {
		String indexfilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_TREASURE)+"\\index.html";
		String menuFilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_TREASURE)+"\\menu.xml";

		// �������g�̑Ώ۔N
		TreeSet<String> ages = new TreeSet<>();
		List<TaggedMaterial> taggedMaterialList = queryAllMementos().getResultList();
		for(TaggedMaterial tm:taggedMaterialList) {
			Material m = tm.getMaterial();
			ages.add(AgeCalculator.calcAgeAsFormattedString("taito", MaterialUtil.getMaterialYearMonthDate(m)));
		}

		// �ڎ��̍쐬
		generateIndexPage(indexfilePath);

		// menu�̍쐬
		generateMenuXml(menuFilePath, ages.descendingSet());

	}

	/**
	 * �ڎ����쐬����
	 * @param indexfilePath
	 * @throws MWException
	 */
	private void generateIndexPage(String indexfilePath) throws MWException {

		// velocity�p�̃}�b�v
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("title","�דl�̔閧�̕�");

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/treasureIndex.html");
		tw.setLevel(1);
		tw.setOutput(indexfilePath);
		tw.out();
	}

	/**
	 * ���j���[�pXML���쐬����
	 * @param listFilePath
	 * @param yyyymms
	 * @throws MWException
	 */
	private void generateMenuXml(String menuFilePath, Set<String> ages) throws MWException {
		List<String> records = new ArrayList<String>();
	    for (String age:ages) {
	      records.add(StringUtil.htmlEncode("<li><a href='../treasures/t_age"+age+".html'>�דl"+age+"�˂̂���</a></li>"));
	    }

	    // velocity �p��map
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("records",records);

	    TemplateWrapper tw = new TemplateWrapper();
	    tw.setContext(map);
	    tw.setTemplate("nobugs/nolife/mw/generator/template/treasureMenu.xml");
	    tw.setLevel(1);
	    tw.setOutput(menuFilePath);
	    tw.out();
	}

	/**
	 * treasure�^�O���t�^���ꂽNOT_IN_USE�ȊO�̑S�Ă�TaggedMaterial���擾���邽�߂̃N�G����ԋp����
	 * @return winner�^�O���t�^���ꂽNOT_IN_USE�ȊO�̑S�Ă�TaggedMaterial���擾���邽�߂�TypedQuery
	 */
	private TypedQuery<TaggedMaterial> queryAllMementos() {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'treasure' AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

}
