package nobugs.nolife.mw.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TAG_CONFIG database table.
 * 
 */
@Entity
@Table(name="TAG_CONFIG", schema = "MW")
public class TagConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=8)
	private String tag;

	@Column(nullable=false, length=16)
	private String category;

	@Column(name="GENERATOR_FQCN", nullable=false, length=256)
	private String generatorFqcn;

	@Column(name="GENERATOR_NAME", nullable=false, length=32)
	private String generatorName;

	@Column(name="SUBGENERATOR_FQCN", nullable=false, length=256)
	private String subgeneratorFqcn;

	public TagConfig() {
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGeneratorFqcn() {
		return this.generatorFqcn;
	}

	public void setGeneratorFqcn(String generatorFqcn) {
		this.generatorFqcn = generatorFqcn;
	}

	public String getGeneratorName() {
		return this.generatorName;
	}

	public void setGeneratorName(String generatorName) {
		this.generatorName = generatorName;
	}

	public String getSubgeneratorFqcn() {
		return this.subgeneratorFqcn;
	}

	public void setSubgeneratorFqcn(String subgeneratorFqcn) {
		this.subgeneratorFqcn = subgeneratorFqcn;
	}

}