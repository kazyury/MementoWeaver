package nobugs.nolife.mw.generator;

import java.util.List;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.MaterialUtil;

public class PartyGenerator extends Generator {

	@Override
	protected String affectedMemento(Material m, String tag) {
		// party‚Ìê‡‚Íyyyy.html
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
