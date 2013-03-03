package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the MEMENTOCONTENTS database table.
 * 
 */
@Embeddable
public class MementoContentsPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique=true, nullable=false, length=8)
	private String mementoId;

	@Column(unique=true, nullable=false, length=14)
	private String materialId;

	@Column(unique=true, nullable=false, length=8)
	private String tag;

	public MementoContentsPK() {
	}
	public String getMementoId() {
		return this.mementoId;
	}
	public void setMementoId(String mementoId) {
		this.mementoId = mementoId;
	}
	public String getMaterialId() {
		return this.materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getTag() {
		return this.tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MementoContentsPK)) {
			return false;
		}
		MementoContentsPK castOther = (MementoContentsPK)other;
		return 
			this.mementoId.equals(castOther.mementoId)
			&& this.materialId.equals(castOther.materialId)
			&& this.tag.equals(castOther.tag);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.mementoId.hashCode();
		hash = hash * prime + this.materialId.hashCode();
		hash = hash * prime + this.tag.hashCode();
		
		return hash;
	}
}