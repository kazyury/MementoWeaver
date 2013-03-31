package nobugs.nolife.mw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.entities.TaggedMaterialPK;

/**
 * Materialユーティリティ
 * @author kazyury
 *
 */
public class MaterialUtil {
	private static Logger logger = Logger.getGlobal();

	/**
	 * 素材の撮影年月日を返却する
	 * @param m
	 * @return
	 */
	public static String getMaterialYearMonthDate(Material m){ return m.getMaterialId().substring(0, 8); }
	public static String getMaterialYearMonth(Material m){ return m.getMaterialId().substring(0, 6); }
	public static String getMaterialYear(Material m) { return m.getMaterialId().substring(0, 4); }
	public static String getMaterialMonth(Material m){ return m.getMaterialId().substring(4, 6); }
	public static String getMaterialDate(Material m){ return m.getMaterialId().substring(6, 8); }
	public static String getMaterialHour(Material m){ return m.getMaterialId().substring(8, 10); }
	public static String getMaterialMinute(Material m){ return m.getMaterialId().substring(10, 12); }
	public static String getMaterialSecond(Material m){ return m.getMaterialId().substring(12, 14); }

	/**
	 * タグ付素材の情報を更新する。
	 * 追加タグのメモについては、chronicleメモ以外の先頭1件を用いる。
	 * @param m
	 * @param tags
	 */
	public static void updateTagInfo(Material m, String[] tags){
		// chronicleではない先頭1件のメモを取得
		String defaultMemo="default memo";
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			if(!tm.getMemo().isEmpty() && !tag.equals("kazunori") && !tag.equals("hiroko") && !tag.equals("taito") ){
				defaultMemo=tm.getMemo();
				break;
			}
		}
		final String memo=defaultMemo;

		// closure的な使い方ってこれであってるんだろうか...。
		MaterialUtil.updateTagInfo(new UpdateMemoHandler() {
			@Override
			public void updateAllMemo(TaggedMaterial existingTm) {/* do nothing */}

			@Override
			public void createNewMemo(TaggedMaterial newTm) {
				MaterialUtil.updateMemo(newTm, memo);
			}
		}, m, tags);
	}

	/**
	 * タグ付素材の情報を更新する。
	 * 追加タグのメモについては、渡されたmemoを用いる。
	 * @param m
	 * @param tags
	 * @param memo
	 */
	public static void updateTagInfo(Material m, String[] tags, final String memo){
		MaterialUtil.updateTagInfo(new UpdateMemoHandler() {

			@Override
			public void updateAllMemo(TaggedMaterial existingTm) {
				MaterialUtil.updateMemo(existingTm, memo);
			}

			@Override
			public void createNewMemo(TaggedMaterial newTm) {
				MaterialUtil.updateMemo(newTm, memo);
			}
		}, m, tags);
	}

	//----------------------------------------------------------------------------

	/**
	 * メモ更新のクロージャインターフェース
	 * @author kazyury
	 *
	 */
	interface UpdateMemoHandler {
		void updateAllMemo(TaggedMaterial existingTm);
		void createNewMemo(TaggedMaterial newTm);
	}
	private static void updateTagInfo(UpdateMemoHandler handler, Material m, String[] tags){
		// String配列をListに詰め替え
		List<String> tagList = new ArrayList<String>();
		for(String tag:tags){
			tagList.add(tag);
		}
		logger.info("tagList is "+tagList.toString());
		/*
		 * taggedMaterial |___|___|___|___|___|
		 * tagList            |___|___|___|___|___|
		 *                  v   v   v   v   v   v
		 *                 upd upd upd upd upd add
		 */
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			/* tagListに含まれる場合は、update/含まれない場合にはNOT_IN_USEに状態を変更
			 * 状態遷移マトリックスは↓
			 *    tm      |        TagList          |
			 * -----------+-------------------------+-----------------
			 * TAG_STATE  | 含まれている	含まれて居ない    |	memo
			 * -----------+-------------------------+-----------------
			 * Staged     | (NoChange)	NotInUse    | 更新する
			 * Published  | (NoChange)	NotInUse    | 更新しない
			 * NotInUse   | Staged		(NoChange)  | 更新しない
			 * -----------+-------------------------+-----------------
			 */
			if(tagList.contains(tag)){
				logger.info("tagList["+tagList.toString()+"]にtag["+tag+"]が含まれています。");

				if(tm.getTagState().equals(Constants.TAG_STATE_STAGED)){
					logger.info("元のタグ状態がSTAGEDのため、素材の状態をSTAGEDに変更します");
					m.setMaterialState(Constants.MATERIAL_STATE_STAGED); // 素材の状態もSTAGEDに変更
					handler.updateAllMemo(tm); // ブロック呼び出し1

				} else if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					logger.info("元のタグ状態がNOT-IN-USEのため、素材の状態をSTAGEDに変更します");
					m.setMaterialState(Constants.MATERIAL_STATE_STAGED); // 素材の状態もSTAGEDに変更
					tm.setTagState(Constants.TAG_STATE_STAGED);
				}
				tagList.remove(tag);
			} else {
				tm.setTagState(Constants.TAG_STATE_NOT_IN_USE);
			}
		}

		// tagListにのみ存在するtagを追加
		for(String tag:tagList){
			TaggedMaterialPK pk = new TaggedMaterialPK();
			pk.setMaterialId(m.getMaterialId());
			pk.setTag(tag);

			TaggedMaterial tm = new TaggedMaterial();
			tm.setId(pk);
			tm.setMaterial(m);
			handler.createNewMemo(tm); // ブロック呼び出し2
			tm.setTagState(Constants.TAG_STATE_STAGED);

			m.getTaggedMaterials().add(tm);
			m.setMaterialState(Constants.MATERIAL_STATE_STAGED);
		}
	}



	/**
	 * メモ設定器
	 * @param tm
	 * @param memo
	 * @return
	 */
	private static TaggedMaterial updateMemo(TaggedMaterial tm, String memo) {
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			logger.info("tag["+tag+"]のためクロニクル・ルールが適用されます");
			tm.setMemo(AgeCalculator.calcAge(tag, MaterialUtil.getMaterialYearMonthDate(m))+"歳の肖像");
		} else {
			tm.setMemo(memo);
		}
		return tm;
	}
}
