package nobugs.nolife.mw.util;

public class Constants {
	// Persistence Unit
	public static String PERSISTENCE_UNIT_NAME="MementoWeaver";

	// Material Type
	public static String MATERIAL_TYPE_JPG="J";
	public static String MATERIAL_TYPE_MOV="M";

	// Material State
	public static String MATERIAL_STATE_INSTALLED="1";
	public static String MATERIAL_STATE_STAGED="2";
	public static String MATERIAL_STATE_IN_USE="3";

	// TaggedMaterial State
	public static String TAG_STATE_STAGED="1";
	public static String TAG_STATE_PUBLISHED="2";
	public static String TAG_STATE_NOT_IN_USE="3";
	
	// Derivatizer
	public static double THUMBNAIL_LONG_SIDE_PIXELS=240.0;
	public static String THUMBNAIL_SUBPATH="thumbnail";

	public static String FFMPEG_PATH="C:\\home\\softwares\\ffmpeg\\ffmpeg ";
	public static String FFMPEG_OPTS=" -f image2 -pix_fmt jpg -ss 1 -s 640x480 -an -y -vframes 1 ";
	
}
