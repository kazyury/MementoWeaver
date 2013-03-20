package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.generator.Generator;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.scene.layout.AnchorPane;

public class GeneratedResultController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;
	private MementoGenerateProcessor processor = new MementoGenerateProcessor();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) {
		this.appl = appMain;
		@SuppressWarnings("unchecked")
		List<Generator> generatorList = (List<Generator>)bulk;
		processor.generateProcess(generatorList);

	}

}
