package nobugs.nolife.mw.util;

public class Constants {
	// pre-defined tag
	public static final String TAG_ALBUM = "album";
	public static final String TAG_KAZUNORI = "kazunori";
	public static final String TAG_HIROKO = "hiroko";
	public static final String TAG_TAITO = "taito";
	public static final String TAG_PARTY = "party";
	public static final String TAG_TREASURE = "treasure";
	public static final String TAG_WINNER = "winner";

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
	
	// dir.properties key
	public static String DIRPROP_KEY_MATERIAL_SOURCE="dir.materialSource";
	public static String DIRPROP_KEY_STAGING_AREA="dir.stagingArea";
	public static String DIRPROP_KEY_MW_ROOT="dir.mw.root";
	public static String DIRPROP_KEY_MW_ALBUM="dir.memento.albums";
	public static String DIRPROP_KEY_MW_CHRONICLE="dir.memento.chronicle";
	public static String DIRPROP_KEY_MW_PRIZE="dir.memento.prizes";
	public static String DIRPROP_KEY_MW_WINNER="dir.memento.winners";
	public static String DIRPROP_KEY_MW_TREASURE="dir.memento.treasures";
	public static String DIRPROP_KEY_MW_PARTY="dir.memento.parties";
	public static String DIRPROP_KEY_MW_MATERIAL="dir.material";

	// scan type
	public static final String SCANTYPE_ALL = "All";
	public static final String SCANTYPE_NOT_IGNORED = "Not Ignored Only";
	public static final String SCANTYPE_IGNORED = "Ignored Only";

	// scan ignored
	public static final String SCAN_NOT_IGNORED = "0";
	public static final String SCAN_IGNORED = "1";

	// memento category
	public static final String MEMENTO_CATEGORY_ALBUM = "albumPage";
	public static final String MEMENTO_CATEGORY_WINNER = "winnerPage";
	public static final String MEMENTO_CATEGORY_TREASURE = "treasurePage";
	public static final String MEMENTO_CATEGORY_CHRONICLE = "chronicle";
	public static final String MEMENTO_CATEGORY_PARTY = "partyPage";


}
