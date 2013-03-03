package nobugs.nolife.mw.persistence;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the MEMENTOCONTENTS database table.
 * 
 */
@Entity
@Table(name="MEMENTOCONTENTS",schema="MW")
public class MementoContents implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MementoContentsPK id;

	public MementoContents() {
	}

	public MementoContentsPK getId() {
		return this.id;
	}

	public void setId(MementoContentsPK id) {
		this.id = id;
	}

}