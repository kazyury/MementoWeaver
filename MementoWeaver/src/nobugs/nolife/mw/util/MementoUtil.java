package nobugs.nolife.mw.util;

import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Memento;

public class MementoUtil {
	private static Logger logger = Logger.getGlobal();
	
	public static boolean isAppendable(Memento memento, String materialId) throws MWException{
		boolean ret = false;
		String category = memento.getCategory();
		String mementoId = memento.getMementoId();
		
		if("albumPage".equals(category)){
			// materialIdがmementoId(ex.a_201301)の年月部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("albumPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("albumPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if("chronicle".equals(category)) {
			logger.info("chronicle["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
			ret = true; // chronicleの場合はいつでもOK
			
		} else if("partyPage".equals(category)) {
			// materialIdがmementoId(ex.p_2013)の年部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("partyPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("partyPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if("treasurePage".equals(category)) {
			// materialId時点の年齢がmementoId(ex.t_age10)の年齢部分と一致しない場合NG
			String age = AgeCalculator.calcAgeAsFormattedString("taito", materialId.substring(0, 9));
			if(age.equals(mementoId.substring(5))){
				logger.info("treasurePage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("treasurePage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else if("winnerPage".equals(category)) {
			// materialIdがmementoId(ex.w_201301)の年部分から始まっていない場合はNG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("winnerPage["+mementoId+"]はMaterialID["+materialId+"]を追加可能です.");
				ret=true;
			} else {
				logger.warning("winnerPage["+mementoId+"]にMaterialID["+materialId+"]を追加することは出来ません.");
				ret=false;
			}
			
		} else {
			throw new MWException("category["+category+"] is not valid. memento was["+memento+"]");
		}

		return ret;
	}
}
