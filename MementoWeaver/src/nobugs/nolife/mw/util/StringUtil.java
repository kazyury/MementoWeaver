package nobugs.nolife.mw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.TaggedMaterial;

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
	 * @throws MWException 
	 */
	public static String[] splitTagString(String joinedTagString) throws MWException{
		if(joinedTagString.startsWith("[") && joinedTagString.endsWith("]")){
			return joinedTagString.substring(1, joinedTagString.lastIndexOf("]")).split("\\]\\[");
		} else {
			throw new MWException("Joined tag string is not valid.");
		}
	}

	/**
	 * 指定された文字列をHTMLエンコードする.
	 * <p>対象文字列は &lt; &gt; &quot; &amp; </p>
	 *
	 * @param message HTMLエンコード対象の文字列
	 * @return HTMLエンコード済みの文字列
	 */
	public static String htmlEncode(String message) {
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<message.length(); i++){
			switch(message.charAt(i)) {
			case '<' :
				ret.append("&lt;");
				break;
			case '>' :
				ret.append("&gt;");
				break;
			case '&' :
				ret.append("&amp;");
				break;
			case '"' :
				ret.append("&quot;");
				break;
			default :
				ret.append(message.charAt(i));
				break;
			}
		}
		return ret.toString();
	}

	/**
	 * 指定された年月日時分秒をRFC1123形式(の一部)に変換する.
	 * 例) Sep 18 2009 00:00:00
	 * DayOfWeekを示す文字は含めない。
	 *
	 * Unit test is available.
	 *
	 * @param year yyyy形式の年部
	 * @param month MM形式の月部
	 * @param date dd形式の日部
	 * @param hour HH形式の時部
	 * @param minute mm形式の分部
	 * @param second ss形式の秒部
	 * @return RFC1123形式(の一部)に変換された文字列
	 * @throws MWException 下位で発生した例外はMWException に変換してthrow する。
	 */
	public static String formatChronicleDate(String year, String month, String date, String hour, String minute, String second) throws MWException {
		// 期待している桁にあうように、0埋めを実施
		year  =  String.format("%04d",Integer.parseInt(year));
		month =  String.format("%02d",Integer.parseInt(month));
		date  =  String.format("%02d",Integer.parseInt(date));
		hour  =  String.format("%02d",Integer.parseInt(hour));
		minute = String.format("%02d",Integer.parseInt(minute));
		second = String.format("%02d",Integer.parseInt(second));

		Date aDate = new Date();
		try {
			aDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(year+month+date+hour+minute+second);
		} catch(ParseException e) {
			logger.severe("日付形式の変換時に例外が発生しました。"+e.getMessage());
			throw new MWException(e);
		}

		logger.fine("was set : "+aDate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss 'GMT'Z",Locale.US);
		logger.fine("after format : "+sdf.format(aDate));
		return sdf.format(aDate);
	}
}
