package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.AgeCalculator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;

public class TreasureGenerator extends Generator {
	private Logger logger = Logger.getGlobal();
	
	@Override
	protected String affectedMemento(Material m, String tag) {
		return getMementoId(m,tag)+".html";
	}

	/** TaggedMaterial tmと同一メメントに属するTaggedMaterialを検索するためのTypedQueryを返却する */
	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(TaggedMaterial tm) {
		Material m = tm.getMaterial();
		int age = AgeCalculator.calcAge("taito", MaterialUtil.getMaterialYearMonthDate(m));
		int dob = AgeCalculator.getDateOfBirth("taito");

		// 例えば、age07.html が渡されたとして、年齢が7歳のマテリアルIDは以下の式で求められる。
		// 7 <= (x-20020306)/10000 < 8
		// 20090306 <= x < 20100306
		String lowerMaterialId = Integer.toString(age*10000 + dob);
		String higherMaterialId = Integer.toString((age+1)*10000 + dob);

		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'treasure' AND tm.id.materialId between :lower AND :higher AND tm.tagState <> :tagState",TaggedMaterial.class);

		logger.info("age:"+age);
		logger.info("lower:"+lowerMaterialId);
		logger.info("higher:"+higherMaterialId);
		query.setParameter("lower", lowerMaterialId);
		query.setParameter("higher", higherMaterialId);
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

	@Override
	protected Memento generateMemento(List<TaggedMaterial> updateTargetList) {
		// 先頭1件のMaterial
		Material m = updateTargetList.get(0).getMaterial();
		int age = AgeCalculator.calcAge("taito", MaterialUtil.getMaterialYearMonthDate(m));
		String outfile = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_TREASURE)+"\\"+affectedMemento(m,null);
		
		// velocity用のマップ
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("g",this);
		map.put("list",updateTargetList);
		map.put("age",age);

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/treasurePage.html");
		tw.setLevel(1);
		tw.setOutput(outfile);
		tw.out();

		// 返却用メメントを生成
		Memento memento = new Memento();
		memento.setMementoId(getMementoId(m));
		memento.setCategory(Constants.MEMENTO_CATEGORY_TREASURE);
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
		return "t_age"+AgeCalculator.calcAge("taito", MaterialUtil.getMaterialYearMonthDate(m)); // treasuresの場合はt_ageXX
	}
}
