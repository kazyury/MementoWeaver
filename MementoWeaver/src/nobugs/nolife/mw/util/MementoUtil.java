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
			// materialId��mementoId(ex.a_201301)�̔N����������n�܂��Ă��Ȃ��ꍇ��NG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("albumPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ��\�ł�.");
				ret=true;
			} else {
				logger.warning("albumPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ����邱�Ƃ͏o���܂���.");
				ret=false;
			}
			
		} else if("chronicle".equals(category)) {
			logger.info("chronicle["+mementoId+"]��MaterialID["+materialId+"]��ǉ��\�ł�.");
			ret = true; // chronicle�̏ꍇ�͂��ł�OK
			
		} else if("partyPage".equals(category)) {
			// materialId��mementoId(ex.p_2013)�̔N��������n�܂��Ă��Ȃ��ꍇ��NG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("partyPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ��\�ł�.");
				ret=true;
			} else {
				logger.warning("partyPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ����邱�Ƃ͏o���܂���.");
				ret=false;
			}
			
		} else if("treasurePage".equals(category)) {
			// materialId���_�̔N�mementoId(ex.t_age10)�̔N����ƈ�v���Ȃ��ꍇNG
			String age = AgeCalculator.calcAgeAsFormattedString("taito", materialId.substring(0, 9));
			if(age.equals(mementoId.substring(5))){
				logger.info("treasurePage["+mementoId+"]��MaterialID["+materialId+"]��ǉ��\�ł�.");
				ret=true;
			} else {
				logger.warning("treasurePage["+mementoId+"]��MaterialID["+materialId+"]��ǉ����邱�Ƃ͏o���܂���.");
				ret=false;
			}
			
		} else if("winnerPage".equals(category)) {
			// materialId��mementoId(ex.w_201301)�̔N��������n�܂��Ă��Ȃ��ꍇ��NG
			if(materialId.startsWith(mementoId.substring(2))){
				logger.info("winnerPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ��\�ł�.");
				ret=true;
			} else {
				logger.warning("winnerPage["+mementoId+"]��MaterialID["+materialId+"]��ǉ����邱�Ƃ͏o���܂���.");
				ret=false;
			}
			
		} else {
			throw new MWException("category["+category+"] is not valid. memento was["+memento+"]");
		}

		return ret;
	}
}
