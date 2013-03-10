package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the MEMENTO database table.
 * 
 */
@Entity
@Table(name="MEMENTO", schema = "MW")
public class Memento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MEMENTO_ID", unique=true, nullable=false, length=8)
	private String mementoId;

	//bi-directional many-to-many association to TaggedMaterial
	@ManyToMany(mappedBy="mementos")
	private List<TaggedMaterial> taggedMaterials;

	public Memento() {
	}

	public String getMementoId() {
		return this.mementoId;
	}

	public void setMementoId(String mementoId) {
		this.mementoId = mementoId;
	}

	public List<TaggedMaterial> getTaggedMaterials() {
		return this.taggedMaterials;
	}

	public void setTaggedMaterials(List<TaggedMaterial> taggedMaterials) {
		this.taggedMaterials = taggedMaterials;
	}

}