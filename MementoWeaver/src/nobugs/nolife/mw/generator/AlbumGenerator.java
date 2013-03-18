package nobugs.nolife.mw.generator;


import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.util.MaterialUtil;

public class AlbumGenerator extends Generator {

	@Override
	public void generate() {
		// TODO Auto-generated method stub

	}

	@Override
	public String affectedMemento(Material m, String tag) {
		return MaterialUtil.getMaterialMonth(m)+".html";
	}

}
