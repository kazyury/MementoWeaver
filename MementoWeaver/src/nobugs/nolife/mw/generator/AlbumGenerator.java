package nobugs.nolife.mw.generator;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;

public class AlbumGenerator extends Generator {

	@Override
	protected String affectedMemento(Material m, String tag) {
		return MaterialUtil.getMaterialYearMonth(m)+".html"; // albumsの場合はyyyymm.html
	}

	/** Material mと同一メメントに属するTaggedMaterialを検索するためのTypedQueryを返却する */
	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(Material m) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
				"WHERE tm.id.tag = 'album' AND tm.id.materialId like :yyyymm AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("yyyymm", MaterialUtil.getMaterialYearMonth(m)+"%");
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}


	@Override
	protected String generateMemento(List<TaggedMaterial> updateTargetList) throws MWException {
		// 先頭1件のMaterial
		Material m = updateTargetList.get(0).getMaterial();
		String year = MaterialUtil.getMaterialYear(m);
		String month = MaterialUtil.getMaterialMonth(m);
		String outfile = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ALBUM)+"\\"+MaterialUtil.getMaterialYearMonth(m)+".html";

		// velocity用のマップ
		Map<String, Object> map = new HashMap<String, Object>();
	    map.put("g",this);
	    map.put("list",updateTargetList);
	    map.put("year",year);
	    map.put("month",month);
	    map.put("title",year+"年"+month+"月のアルバム");
	    
	    TemplateWrapper tw = new TemplateWrapper();
	    tw.setContext(map);
	    tw.setTemplate("nobugs/nolife/mw/generator/template/albumPage.html");
	    tw.setLevel(1);
	    tw.setOutput(outfile);
	    tw.out();

	    return outfile;
	}
	
}
