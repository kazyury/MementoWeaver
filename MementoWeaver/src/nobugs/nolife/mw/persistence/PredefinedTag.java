package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PREDEFINEDTAG database table.
 * 
 */
@Entity
@Table(name="PREDEFINEDTAG",schema="MW")
public class PredefinedTag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=8)
	private String tag;

	@Column(nullable=false, length=256)
	private String fqcn;

	public PredefinedTag() {
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getFqcn() {
		return this.fqcn;
	}

	public void setFqcn(String fqcn) {
		this.fqcn = fqcn;
	}

}