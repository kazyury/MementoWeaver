package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.scene.layout.AnchorPane;

public class GeneratedResultController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) throws MWException {
		this.appl = appMain;

		MementoGenerateProcessor processor = new MementoGenerateProcessor();
		List<String> generatedMemento = processor.generateProcess();
		// TODO ê∂ê¨ÇµÇΩÉÅÉÅÉìÉgÇÃâÊñ ï\é¶

	}

}
