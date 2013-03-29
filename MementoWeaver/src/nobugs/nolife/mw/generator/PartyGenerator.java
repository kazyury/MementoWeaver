package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;

public class PartyGenerator extends Generator {

	@Override
	protected String affectedMemento(Material m, String tag) {
		return getMementoId(m,tag)+".html";
	}

	/** TaggedMaterial tmと同一メメントに属するTaggedMaterialを検索するためのTypedQueryを返却する */
	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(TaggedMaterial tm) {
		Material m = tm.getMaterial();
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'party' AND tm.id.materialId like :yyyy AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("yyyy", MaterialUtil.getMaterialYear(m)+"%");
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

	@Override
	protected Memento generateMemento(List<TaggedMaterial> updateTargetList) throws MWException {

		// 先頭1件のMaterial
		Material m = updateTargetList.get(0).getMaterial();
		String year = MaterialUtil.getMaterialYear(m);
		String outfile = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_PARTY)+"\\"+affectedMemento(m,null);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("g",this);
		map.put("list",updateTargetList);
		map.put("year",year);

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/albumPage.html");
		tw.setLevel(1);
		tw.setOutput(outfile);
		tw.out();

		// 返却用メメントを生成
		Memento memento = new Memento();
		memento.setMementoId(getMementoId(m));
		memento.setCategory("partyPage");
		memento.setProductionPath(outfile);
		 // mapped by mementos(TaggedMaterial)なので、TaggedMaterial側でmementosを設定
		for(TaggedMaterial tm:updateTargetList) {
			tm.getMementos().add(memento);
		}
		memento.setTaggedMaterials(updateTargetList);

		return memento;
	}

	@Override
	protected String getMementoId(Material m, String tag) {
		return getMementoId(m);
	}

	private String getMementoId(Material m) {
		return "p_"+MaterialUtil.getMaterialYear(m); // Partyの場合はp_yyyy
	}
	
}
