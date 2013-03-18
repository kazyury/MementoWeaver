package nobugs.nolife.mw.util;

import java.util.List;
import java.util.logging.Logger;

import nobugs.nolife.mw.persistence.TaggedMaterial;

public class StringUtil {
	private static Logger logger = Logger.getGlobal();
	/**
	 * 連結したタグ名を返却する。連結タグ名の形式は[タグ名][タグ名]...
	 * @param taggedMaterialList
	 * @return
	 */
	public static String joinTagString(List<TaggedMaterial> taggedMaterialList) {
		StringBuffer joinedTags=new StringBuffer();
		for (TaggedMaterial tm:taggedMaterialList) {
			if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
				joinedTags.append("["+tm.getId().getTag()+"]");
			}
		}
		return joinedTags.toString();
	}
	
	/**
	 * 連結したタグ名をString配列に分解して返却する。
	 * @param joinedTagString
	 * @return
	 */
	public static String[] splitTagString(String joinedTagString){
		if(joinedTagString.startsWith("[") && joinedTagString.endsWith("]")){
			return joinedTagString.substring(1, joinedTagString.lastIndexOf("]")).split("\\]\\[");
		} else {
			// TODO 例外スロー
			logger.warning("Joined tag string is not valid.");
			return null;
		}
	}
}
