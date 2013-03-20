package nobugs.nolife.mw.generator;


import java.util.List;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;

public class AlbumGenerator extends Generator {

	@Override
	protected String affectedMemento(Material m, String tag) {
		return MaterialUtil.getMaterialMonth(m)+".html"; // albums‚Ìê‡‚Íyyyymm.html
	}

	/** Material m‚Æ“¯ˆêƒƒƒ“ƒg‚É‘®‚·‚éTaggedMaterial‚ğŒŸõ‚·‚é‚½‚ß‚ÌTypedQuery‚ğ•Ô‹p‚·‚é */
	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(Material m) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
				"WHERE tm.id.tag = 'album' AND tm.id.materialId like :yyyymm AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("yyyymm", MaterialUtil.getMaterialMonth(m)+"%");
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}


	@Override
	protected String generateMemento(List<TaggedMaterial> updateTargetList) {
		// TODO Auto-generated method stub
		return null;
	}
}
