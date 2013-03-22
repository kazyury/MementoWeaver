package nobugs.nolife.mw.util;

import java.util.List;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.TaggedMaterial;

public class StringUtil {
	/**
	 * �A�������^�O����ԋp����B�A���^�O���̌`����[�^�O��][�^�O��]...
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
	 * �A�������^�O����String�z��ɕ������ĕԋp����B
	 * @param joinedTagString
	 * @return
	 * @throws MWException 
	 */
	public static String[] splitTagString(String joinedTagString) throws MWException{
		if(joinedTagString.startsWith("[") && joinedTagString.endsWith("]")){
			return joinedTagString.substring(1, joinedTagString.lastIndexOf("]")).split("\\]\\[");
		} else {
			throw new MWException("Joined tag string is not valid.");
		}
	}
}
