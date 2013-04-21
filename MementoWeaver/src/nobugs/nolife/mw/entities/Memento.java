package nobugs.nolife.mw.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.MERGE;


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

	@Column(length=16)
	private String category;

	@Column(name="PRODUCTION_PATH", length=256)
	private String productionPath;

	//bi-directional many-to-many association to TaggedMaterial
	@ManyToMany(mappedBy="mementos", cascade = { PERSIST, MERGE })
	private List<TaggedMaterial> taggedMaterials;

	//bi-directional many-to-one association to ScannedResult
	@OneToMany(mappedBy="memento")
	private List<ScannedResult> scannedResults;

	public Memento() {
	}

	public String getMementoId() {
		return this.mementoId;
	}

	public void setMementoId(String mementoId) {
		this.mementoId = mementoId;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProductionPath() {
		return this.productionPath;
	}

	public void setProductionPath(String productionPath) {
		this.productionPath = productionPath;
	}

	public List<TaggedMaterial> getTaggedMaterials() {
		return this.taggedMaterials;
	}

	public void setTaggedMaterials(List<TaggedMaterial> taggedMaterials) {
		this.taggedMaterials = taggedMaterials;
	}

	public List<ScannedResult> getScannedResults() {
		return this.scannedResults;
	}

	public void setScannedResults(List<ScannedResult> scannedResults) {
		this.scannedResults = scannedResults;
	}

	public ScannedResult addScannedResult(ScannedResult scannedResult) {
		getScannedResults().add(scannedResult);
		scannedResult.setMemento(this);

		return scannedResult;
	}

	public ScannedResult removeScannedResult(ScannedResult scannedResult) {
		getScannedResults().remove(scannedResult);
		scannedResult.setMemento(null);

		return scannedResult;
	}

}