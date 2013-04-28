package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.StringUtil;


public class PartyIndexGenerator extends SubGenerator {

	@Override
	protected void generateSubMemento() {
		String indexfilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_PARTY)+"\\index.html";
		String menuFilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_PARTY)+"\\menu.xml";

		// メメントの対象年
		TreeSet<String> yyyys = new TreeSet<>();
		List<TaggedMaterial> taggedMaterialList = queryAllMementos().getResultList();
		for(TaggedMaterial tm:taggedMaterialList) {
			Material m = tm.getMaterial();
			yyyys.add(MaterialUtil.getMaterialYear(m));
		}

		// 目次の作成
		generateIndexPage(indexfilePath);

		// menuの作成
		generateMenuXml(menuFilePath, yyyys.descendingSet());

	}

	/**
	 * 目次を作成する
	 * @param indexfilePath
	 * @throws MWException
	 */
	private void generateIndexPage(String indexfilePath) {

		// velocity用のマップ
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("title","我が家の素敵なごはん");

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/partyIndex.html");
		tw.setLevel(1);
		tw.setOutput(indexfilePath);
		tw.out();
	}

	/**
	 * メニュー用XMLを作成する
	 * @param listFilePath
	 * @param yyyymms
	 * @throws MWException
	 */
	private void generateMenuXml(String menuFilePath, Set<String> yyyys) {
		List<String> records = new ArrayList<String>();
	    for (String yyyy:yyyys) {
	      records.add(StringUtil.htmlEncode("<li><a href='../parties/p_"+yyyy+".html'>"+yyyy+"年の素敵なごはん</a></li>"));
	    }

	    // velocity 用のmap
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    map.put("records",records);

	    TemplateWrapper tw = new TemplateWrapper();
	    tw.setContext(map);
	    tw.setTemplate("nobugs/nolife/mw/generator/template/partyMenu.xml");
	    tw.setLevel(1);
	    tw.setOutput(menuFilePath);
	    tw.out();
	}

	/**
	 * partyタグが付与されたNOT_IN_USE以外の全てのTaggedMaterialを取得するためのクエリを返却する
	 * @return partyタグが付与されたNOT_IN_USE以外の全てのTaggedMaterialを取得するためのTypedQuery
	 */
	private TypedQuery<TaggedMaterial> queryAllMementos() {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'party' AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

}
