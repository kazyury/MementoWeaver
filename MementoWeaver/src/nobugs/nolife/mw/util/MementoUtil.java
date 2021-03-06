package nobugs.nolife.mw.util;

import java.util.logging.Logger;

import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.exceptions.MWImplementationError;

public class MementoUtil {
	private static Logger logger = Logger.getGlobal();
	
	public static boolean isAppendable(Memento memento, String materialId) {
		boolean ret = false;
		String category = memento.getCategory();
		String mementoId = memento.getMementoId();
		
		if(Constants.MEMENTO_CATEGORY_ALBUM.equals(category)){
			// materialIdがmementoId(ex.a_201301)の年月部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("albumPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("albumPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if(Constants.MEMENTO_CATEGORY_CHRONICLE.equals(category)) {
			logger.info("chronicle["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
			ret = true; // chronicleの場合はいつでもOK
			
		} else if(Constants.MEMENTO_CATEGORY_PARTY.equals(category)) {
			// materialIdがmementoId(ex.p_2013)の年部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("partyPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("partyPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if(Constants.MEMENTO_CATEGORY_TREASURE.equals(category)) {
			// materialId時点の年齢がmementoId(ex.t_age10)の年齢部分と一致しない場合NG
			String age = AgeCalculator.calcAgeAsFormattedString("taito", materialId.substring(0, 9));
			if(age.equals(mementoId.substring(5))){
				logger.info("treasurePage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("treasurePage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if(Constants.MEMENTO_CATEGORY_WINNER.equals(category)) {
			// materialIdがmementoId(ex.w_201301)の年部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("winnerPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("winnerPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else {
			throw new MWImplementationError("category["+category+"] is not valid. memento was["+memento+"]");
		}

		return ret;
	}
}
