package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MATERIAL database table.
 * 
 */
@Entity
@Table(name="MATERIAL",schema="MW")
public class Material implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=14)
	private String materialId;

	@Column(name="CREATED_MONTH", nullable=false)
	private int createdMonth;

	@Column(name="CREATED_YEAR", nullable=false)
	private int createdYear;

	@Column(nullable=false, length=1)
	private String materialState;

	@Column(nullable=false, length=1)
	private String materialType;

	public Material() {
	}

	public String getMaterialId() {
		return this.materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public int getCreatedMonth() {
		return this.createdMonth;
	}

	public void setCreatedMonth(int createdMonth) {
		this.createdMonth = createdMonth;
	}

	public int getCreatedYear() {
		return this.createdYear;
	}

	public void setCreatedYear(int createdYear) {
		this.createdYear = createdYear;
	}

	public String getMaterialState() {
		return this.materialState;
	}

	public void setMaterialState(String materialState) {
		this.materialState = materialState;
	}

	public String getMaterialType() {
		return this.materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

}