package nobugs.nolife.mw.dto;

import java.nio.file.Path;

public class ScannedMaterialDTO {
	private Path path;
	private String mateiralId;
	private boolean registeredMaterial;
	private boolean belongToMemento;
	
	public boolean isBelongToMemento() {
		return belongToMemento;
	}
	public void setBelongToMemento(boolean belongToMemento) {
		this.belongToMemento = belongToMemento;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	public String getMateiralId() {
		return mateiralId;
	}
	public void setMateiralId(String mateiralId) {
		this.mateiralId = mateiralId;
	}
	public boolean isRegisteredMaterial() {
		return registeredMaterial;
	}
	public void setRegisteredMaterial(boolean registeredMaterial) {
		this.registeredMaterial = registeredMaterial;
	}
	
}
