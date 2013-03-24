package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;


public class AlbumIndexGenerator extends SubGenerator {

	@Override
	protected void generateSubMemento() throws MWException {
	    String indexfilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ALBUM)+"\\index.html";
		
		// アルバムのyyyy一覧
		TreeSet<String> yyyys = new TreeSet<>();	// TODO implement
		// アルバムのyyyymm一覧
		TreeSet<String> yyyymms = new TreeSet<>();	// TODO implement

		// yyyys, yyyymms の設定
		List<TaggedMaterial> taggedMaterialList = queryAllMementos().getResultList();
		for(TaggedMaterial tm:taggedMaterialList) {
			Material m = tm.getMaterial();
			yyyys.add(MaterialUtil.getMaterialYear(m));
			yyyymms.add(MaterialUtil.getMaterialYearMonth(m));
		}

		// velocity用のマップ
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yyyys",yyyys.descendingSet());
		map.put("yyyymms",yyyymms);

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/albumIndex.html");
		tw.setLevel(1);
		tw.setOutput(indexfilePath);
		tw.out();
		
	}


	private TypedQuery<TaggedMaterial> queryAllMementos() {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'album' AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

}
