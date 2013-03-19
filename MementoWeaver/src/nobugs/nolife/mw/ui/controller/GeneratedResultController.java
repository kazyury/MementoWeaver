package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.scene.layout.AnchorPane;

public class GeneratedResultController extends AnchorPane implements MWSceneController {
	private static Logger logger = Logger.getGlobal();
	private AppMain appl;
	private MementoGenerateProcessor processor = new MementoGenerateProcessor();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		logger.info(processor.toString()+"‚ğ—p‚¢‚Äƒƒƒ“ƒg‚ğ¶¬‚µ‚Ü‚·");
		try {
			processor.generate();
		} catch (MWException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setApplication(AppMain appMain, Object bulk) {
		this.appl = appMain;

	}

}
