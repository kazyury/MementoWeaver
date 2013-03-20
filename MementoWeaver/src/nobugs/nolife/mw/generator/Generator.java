package nobugs.nolife.mw.generator;

import java.util.ArrayList;
import java.util.List;

import nobugs.nolife.mw.persistence.Material;

public abstract class Generator {
	protected List<String> mementoList = new ArrayList<String>();

	public abstract void generate();
	public abstract String affectedMemento(Material m, String tag);

	
	public void prepare(Material m, String tag){
		String memento = affectedMemento(m, tag);
		if(!mementoList.contains(memento) && memento!=null){
			mementoList.add(affectedMemento(m, tag));
		}
	}
	
	public List<String> preparedMemento() {
		return mementoList;
	}
}
