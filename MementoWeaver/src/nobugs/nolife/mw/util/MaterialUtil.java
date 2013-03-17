package nobugs.nolife.mw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nobugs.nolife.mw.image.ImageManipulator;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.persistence.TaggedMaterial;
import nobugs.nolife.mw.persistence.TaggedMaterialPK;

/**
 * Materialユーティリティ
 * @author kazyury
 *
 */
public class MaterialUtil {

	/** 
	 * 画像を回転して保管する。静止画素材本体とサムネイルが対象となる。
	 * @param m
	 * @param degree:回転角
	 */
	public static void rotatePhoto(Material m,int degree){
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			System.out.println("動画素材は回転に対応していません"); // TODO 例外送出
			return;
		}

		try {
			// 素材本体を回転する。
			ImageManipulator.rotate(PathUtil.getInstalledPhotoPath(m), degree);
			// サムネイルを回転する。
			ImageManipulator.rotate(PathUtil.getInstalledThumbnailPath(m), degree);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * yyyymmdd形式で素材の撮影日を返却する
	 * @param m
	 * @return
	 */
	public static String getMaterialDate(Material m){
		return m.getMaterialId().substring(0, 8);
	}

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
		System.out.println("tagList is "+tagList.toString());
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
			 *            |        TagList
			 * -----------+-------------------------
			 * TAG_STATE  | 含まれている	含まれて居ない
			 * -----------+-------------------------
			 * Staged     | (NoChange)	NotInUse
			 * Published  | (NoChange)	NotInUse
			 * NotInUse   | Staged		(NoChange)
			 * -----------+-------------------------
			 */
			if(tagList.contains(tag)){
				handler.updateAllMemo(tm); // ブロック呼び出し1
				tagList.remove(tag);
				if(tm.getTagState().equals(Constants.TAG_STATE_NOT_IN_USE)){
					tm.setTagState(Constants.TAG_STATE_STAGED);
				}
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
		}
	}



	/**
	 * メモ設定器
	 * @param tm
	 * @param memo
	 * @return
	 */
	private static TaggedMaterial updateMemo(TaggedMaterial tm, String memo) {
		PropertyUtil prop = new PropertyUtil();
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			System.out.println("chronicle rule applied");
			tm.setMemo(prop.calcAge(tag, MaterialUtil.getMaterialDate(m))+"歳の肖像");
		} else {
			tm.setMemo(memo);
		}
		return tm;
	}
}
