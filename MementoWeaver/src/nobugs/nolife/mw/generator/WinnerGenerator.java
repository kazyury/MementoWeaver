package nobugs.nolife.mw.generator;

import java.util.List;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.MaterialUtil;

public class WinnerGenerator extends Generator {
	@Override
	protected String affectedMemento(Material m, String tag) {
		// winner‚Ìê‡‚Íyyyy.html
		return MaterialUtil.getMaterialYear(m)+".html";
	}

	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(
			Material m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String generateMemento(List<TaggedMaterial> updateTargetList) {
		// TODO Auto-generated method stub
		return null;
	}

}
