package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MEMENTO database table.
 * 
 */
@Entity
@Table(name="MEMENTO",schema="MW")
public class Memento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false, length=8)
	private String mementoId;

	public Memento() {
	}

	public String getMementoId() {
		return this.mementoId;
	}

	public void setMementoId(String mementoId) {
		this.mementoId = mementoId;
	}

}