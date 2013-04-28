package nobugs.nolife.mw.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.processing.MementoGenerateProcessor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class GeneratedResultController extends AnchorPane implements MWSceneController {
	private AppMain appl;
	
	@FXML protected void exit(ActionEvent e) {Platform.exit();}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) { /* nothing to do */	}

	@Override
	public void setApplication(AppMain appMain, Object bulk){
		this.appl = appMain;

		MementoGenerateProcessor processor = new MementoGenerateProcessor();
		List<String> generatedMemento = processor.generateProcess();
		// TODO ê∂ê¨ÇµÇΩÉÅÉÅÉìÉgÇÃâÊñ ï\é¶

	}

}
