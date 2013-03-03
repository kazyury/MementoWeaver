package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TAGGEDMATERIAL database table.
 * 
 */
@Entity
@Table(name="TAGGEDMATERIAL",schema="MW")
public class TaggedMaterial implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TaggedMaterialPK id;

	@Column(length=256)
	private String memo;

	@Column(nullable=false, length=1)
	private String tagState;

	public TaggedMaterial() {
	}

	public TaggedMaterialPK getId() {
		return this.id;
	}

	public void setId(TaggedMaterialPK id) {
		this.id = id;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTagState() {
		return this.tagState;
	}

	public void setTagState(String tagState) {
		this.tagState = tagState;
	}

}