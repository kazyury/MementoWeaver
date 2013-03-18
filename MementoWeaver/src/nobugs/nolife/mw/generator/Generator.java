package nobugs.nolife.mw.generator;

import nobugs.nolife.mw.persistence.Material;

public abstract class Generator {
	public abstract void generate();
	public abstract String affectedMemento(Material m, String tag);

}
