package nobugs.nolife.mw.util;

import java.util.List;

import nobugs.nolife.mw.persistence.TaggedMaterial;

public class StringUtil {
	/**
	 * �A�������^�O����ԋp����B�A���^�O���̌`����[�^�O��][�^�O��]...
	 * @param taggedMaterialList
	 * @return
	 */
	public static String joinTagString(List<TaggedMaterial> taggedMaterialList) {
		StringBuffer joinedTags=new StringBuffer();
		for (TaggedMaterial tm:taggedMaterialList) {
			if(!tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
				joinedTags.append("["+tm.getId().getTag()+"]");
			}
		}
		return joinedTags.toString();
	}
	
	/**
	 * �A�������^�O����String�z��ɕ������ĕԋp����B
	 * @param joinedTagString
	 * @return
	 */
	public static String[] splitTagString(String joinedTagString){
		if(joinedTagString.startsWith("[") && joinedTagString.endsWith("]")){
			return joinedTagString.substring(1, joinedTagString.lastIndexOf("]")).split("\\]\\[");
		} else {
			// TODO ��O�X���[
			System.out.println("Joined tag string is not valid.");
			return null;
		}
	}
}
