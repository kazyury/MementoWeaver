package nobugs.nolife.mw.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TAGGED_MATERIAL database table.
 * 
 */
@Entity
@Table(name="TAGGED_MATERIAL", schema = "MW")
public class TaggedMaterial implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TaggedMaterialPK id;

	@Column(length=256)
	private String memo;

	@Column(name="TAG_STATE", length=1)
	private String tagState;

	//bi-directional many-to-many association to Memento
	@ManyToMany
	@JoinTable(
		name="MEMENTO_CONTENTS"
		, joinColumns={
			@JoinColumn(name="MATERIAL_ID", referencedColumnName="MATERIAL_ID", nullable=false),
			@JoinColumn(name="TAG", referencedColumnName="TAG", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="MEMENTO_ID", nullable=false)
			}, schema = "MW"
		)
	private List<Memento> mementos;

	//bi-directional many-to-one association to Material
	@ManyToOne
	@JoinColumn(name="MATERIAL_ID", nullable=false, insertable=false, updatable=false)
	private Material material;

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

	public List<Memento> getMementos() {
		return this.mementos;
	}

	public void setMementos(List<Memento> mementos) {
		this.mementos = mementos;
	}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	
}