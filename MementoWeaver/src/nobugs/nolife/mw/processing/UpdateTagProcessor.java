package nobugs.nolife.mw.processing;

import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.entities.TaggedMaterialPK;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWImplementationError;
import nobugs.nolife.mw.util.AgeCalculator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class UpdateTagProcessor {
	private Logger logger = Logger.getGlobal();

	/**
	 * Material mの状態を更新・保管する。(新規追加タグのメモはデフォルト値で更新)
	 * @param m
	 * @param toggleButtonState:画面上のタグ設定内容
	 * @throws MWException 
	 */
	public void updateTagProess(Material m, Map<String, Boolean> toggleButtonState) {
		// 既にDBに登録されているタグの処理
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			// 画面側の選択状況(DBに登録されているが画面で入力されていないカスタムタグはfalse扱い)
			Boolean selected = toggleButtonState.get(tag);
			if(selected==null){ selected = new Boolean(false); }
			logger.info("DB登録済みタグ["+tag+"]の処理を行います。画面選択状況は["+selected.toString()+"]");

			// タグ状態の更新(デシジョンテーブル：MW_SceneDesign[EditMaterial補足(1)]を参照)
			updateTagState(tm, selected);

			// 画面入力が無いので既にDB登録されているタグのメモは更新しない.

			// 画面上のタグ設定内容より、処理を終えた分を除去
			toggleButtonState.remove(tag);
		}

		// 画面で新規に設定されてたタグの処理(DBには存在しない)
		String defaultMemo = selectDefaultMemo(m);
		createAddedTagEntry(m, toggleButtonState, defaultMemo);

		// Materialの状態更新と永続化
		MaterialUtil.updateMaterialState(m);
		storeMaterial(m);
	}

	/**
	 * Material mの状態を更新・保管する。(全てのタグのメモは指定の値)
	 * @param m
	 * @param toggleButtonState:画面上のタグ設定内容
	 * @param memo
	 * @throws MWException 
	 */
	public void updateTagProess(Material m, Map<String, Boolean> toggleButtonState, String memo) {

		// 既にDBに登録されているタグの処理
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			// 画面側の選択状況(DBに登録されているが画面で入力されていないカスタムタグはfalse扱い)
			Boolean selected = toggleButtonState.get(tag);
			if(selected==null){ selected = new Boolean(false); }
			logger.info("DB登録済みタグ["+tag+"]の処理を行います。画面選択状況は["+selected.toString()+"]");

			// タグ状態の更新(デシジョンテーブル：MW_SceneDesign[EditMaterial補足(1)]を参照)
			updateTagState(tm, selected);

			// 画面入力されたメモでDB登録されているタグのメモを、Published以外ならば更新.
			if(!tm.getTagState().equals(Constants.TAG_STATE_PUBLISHED)){
				updateMemo(tm, memo);
			}

			// 画面上のタグ設定内容より、処理を終えた分を除去
			toggleButtonState.remove(tag);
		}

		// 画面で新規に設定されてたタグの処理(DBには存在しない)
		createAddedTagEntry(m, toggleButtonState, memo);

		// Materialの状態更新と永続化
		MaterialUtil.updateMaterialState(m);
		storeMaterial(m);
	}

	/**
	 * Materialを永続化する
	 * @param m
	 */
	private void storeMaterial(Material m){
		EntityManager em = PersistenceUtil.getMWEntityManager();
		em.getTransaction().begin();
		em.merge(m);
		em.getTransaction().commit();
		em.close();
	}

	/**
	 * Material mのうちchronicleではないタグの先頭1件目のメモを返却する
	 * @param m
	 * @return
	 */
	private String selectDefaultMemo(Material m) {
		// chronicleではない先頭1件のメモを取得
		String defaultMemo="";
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			if(!tm.getMemo().isEmpty() && !tag.equals("kazunori") && !tag.equals("hiroko") && !tag.equals("taito") ){
				defaultMemo=tm.getMemo();
				break;
			}
		}
		logger.info("新規登録用デフォルトメモは["+defaultMemo+"]です.");
		return defaultMemo;
	}

	/**
	 * タグ状態を更新する(デシジョンテーブル：MW_SceneDesign[EditMaterial補足(1)]を参照)
	 * @param tm 更新対象のTaggedMaterial
	 * @param isSelected 画面上でそのタグが選択されているか否か
	 * @throws MWException
	 */
	private void updateTagState(TaggedMaterial tm, Boolean isSelected){
		logger.info("タグ["+tm.getId().getTag()+"]の状態更新処理を行います。");
		// DB側の状態
		String state = tm.getTagState();

		// 状態判定
		if(state.equals(Constants.TAG_STATE_STAGED)){
			if(isSelected.booleanValue()){
				logger.info("作成2：TaggedMaterial状態は変更しません.");
			} else {
				logger.info("削除1：TaggedMaterial状態を3(Not In Use)に更新します.");
				tm.setTagState(Constants.TAG_STATE_NOT_IN_USE);
			}
		} else if(state.equals(Constants.TAG_STATE_PUBLISHED)){
			if(isSelected.booleanValue()){
				logger.info("Publishedタグの状態は変更しません.");
			} else {
				// 例外：Bug(PublishedではDisable制御している)
				throw new MWImplementationError("[BUG] TAG already Published.Cannot remove.");
			}
		} else if(state.equals(Constants.TAG_STATE_NOT_IN_USE)){
			if(isSelected.booleanValue()){
				logger.info("作成3：TaggedMaterial状態をStagedに更新");
				tm.setTagState(Constants.TAG_STATE_STAGED);
			} else {
				logger.info("NotInUseタグの状態は変更しません.");
			}
		} else {
			// 例外:Bug
			throw new MWImplementationError("[BUG] Unknwon tag state["+state+"].");
		}
	}

	/**
	 * 画面上で追加されたタグエントリに対応するTaggedMaterialを新規に作成し、Material mに関連付ける
	 * @param m
	 * @param toggleButtonState
	 * @param memo
	 */
	private void createAddedTagEntry(Material m, Map<String, Boolean> toggleButtonState, String memo) {
		for(Map.Entry<String, Boolean> entry:toggleButtonState.entrySet()) {
			if(entry.getValue().booleanValue()) {
				logger.fine("タグ:"+entry.getKey()+"は選択されていますが、DB未登録のため新規TaggedMaterialを作成します.");
				// 作成1：TaggedMaterialを作成。
				TaggedMaterialPK pk = new TaggedMaterialPK();
				pk.setMaterialId(m.getMaterialId());
				pk.setTag(entry.getKey());

				TaggedMaterial taggedMaterial = new TaggedMaterial();
				taggedMaterial.setId(pk);
				taggedMaterial.setMaterial(m);
				updateMemo(taggedMaterial, memo); // クロニクル系ではメモの特別ルール在り
				taggedMaterial.setTagState(Constants.TAG_STATE_STAGED);

				m.getTaggedMaterials().add(taggedMaterial);
			} else {
				// no action
			}
		}
	}
	
	/**
	 * クロニクル系のメモ設定ルールを実装したメモ設定器
	 * @param tm
	 * @param memo
	 * @return
	 */
	private void updateMemo(TaggedMaterial tm, String memo) {
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			logger.info("tag["+tag+"]のためクロニクル・ルールが適用されます");
			tm.setMemo(AgeCalculator.calcAge(tag, MaterialUtil.getMaterialYearMonthDate(m))+"歳の肖像");
		} else {
			tm.setMemo(memo);
		}
	}

}
