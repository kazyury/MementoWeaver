package nobugs.nolife.mw.ui.controller;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.MWException;
import javafx.fxml.Initializable;
// ���Controller�n��Business Processing�n�𕪗�
public interface MWSceneController extends Initializable {
	public void setApplication(AppMain appMain, Object bulk) throws MWException;
}
