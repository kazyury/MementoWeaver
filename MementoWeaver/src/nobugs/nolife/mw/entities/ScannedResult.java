package nobugs.nolife.mw.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the SCANNED_RESULTS database table.
 * 
 */
@Entity
@Table(name="SCANNED_RESULTS", schema = "MW")
public class ScannedResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PRODUCTION_PATH", unique=true, nullable=false, length=256)
	private String productionPath;

	@Column(length=1)
	private String ignored;

	@Temporal(TemporalType.DATE)
	@Column(name="LAST_SCANNED")
	private Date lastScanned;

	//bi-directional many-to-one association to Memento
	@ManyToOne
	@JoinColumn(name="MEMENTO_ID")
	private Memento memento;

	public ScannedResult() {
	}

	public String getProductionPath() {
		return this.productionPath;
	}

	public void setProductionPath(String productionPath) {
		this.productionPath = productionPath;
	}

	public String getIgnored() {
		return this.ignored;
	}

	public void setIgnored(String ignored) {
		this.ignored = ignored;
	}

	public Date getLastScanned() {
		return this.lastScanned;
	}

	public void setLastScanned(Date lastScanned) {
		this.lastScanned = lastScanned;
	}

	public Memento getMemento() {
		return this.memento;
	}

	public void setMemento(Memento memento) {
		this.memento = memento;
	}

}