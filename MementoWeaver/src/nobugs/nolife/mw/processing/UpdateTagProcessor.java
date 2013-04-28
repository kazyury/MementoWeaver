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
	 * Material m�̏�Ԃ��X�V�E�ۊǂ���B(�V�K�ǉ��^�O�̃����̓f�t�H���g�l�ōX�V)
	 * @param m
	 * @param toggleButtonState:��ʏ�̃^�O�ݒ���e
	 * @throws MWException 
	 */
	public void updateTagProess(Material m, Map<String, Boolean> toggleButtonState) {
		// ����DB�ɓo�^����Ă���^�O�̏���
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			// ��ʑ��̑I����(DB�ɓo�^����Ă��邪��ʂœ��͂���Ă��Ȃ��J�X�^���^�O��false����)
			Boolean selected = toggleButtonState.get(tag);
			if(selected==null){ selected = new Boolean(false); }
			logger.info("DB�o�^�ς݃^�O["+tag+"]�̏������s���܂��B��ʑI���󋵂�["+selected.toString()+"]");

			// �^�O��Ԃ̍X�V(�f�V�W�����e�[�u���FMW_SceneDesign[EditMaterial�⑫(1)]���Q��)
			updateTagState(tm, selected);

			// ��ʓ��͂������̂Ŋ���DB�o�^����Ă���^�O�̃����͍X�V���Ȃ�.

			// ��ʏ�̃^�O�ݒ���e���A�������I������������
			toggleButtonState.remove(tag);
		}

		// ��ʂŐV�K�ɐݒ肳��Ă��^�O�̏���(DB�ɂ͑��݂��Ȃ�)
		String defaultMemo = selectDefaultMemo(m);
		createAddedTagEntry(m, toggleButtonState, defaultMemo);

		// Material�̏�ԍX�V�Ɖi����
		MaterialUtil.updateMaterialState(m);
		storeMaterial(m);
	}

	/**
	 * Material m�̏�Ԃ��X�V�E�ۊǂ���B(�S�Ẵ^�O�̃����͎w��̒l)
	 * @param m
	 * @param toggleButtonState:��ʏ�̃^�O�ݒ���e
	 * @param memo
	 * @throws MWException 
	 */
	public void updateTagProess(Material m, Map<String, Boolean> toggleButtonState, String memo) {

		// ����DB�ɓo�^����Ă���^�O�̏���
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			// ��ʑ��̑I����(DB�ɓo�^����Ă��邪��ʂœ��͂���Ă��Ȃ��J�X�^���^�O��false����)
			Boolean selected = toggleButtonState.get(tag);
			if(selected==null){ selected = new Boolean(false); }
			logger.info("DB�o�^�ς݃^�O["+tag+"]�̏������s���܂��B��ʑI���󋵂�["+selected.toString()+"]");

			// �^�O��Ԃ̍X�V(�f�V�W�����e�[�u���FMW_SceneDesign[EditMaterial�⑫(1)]���Q��)
			updateTagState(tm, selected);

			// ��ʓ��͂��ꂽ������DB�o�^����Ă���^�O�̃������APublished�ȊO�Ȃ�΍X�V.
			if(!tm.getTagState().equals(Constants.TAG_STATE_PUBLISHED)){
				updateMemo(tm, memo);
			}

			// ��ʏ�̃^�O�ݒ���e���A�������I������������
			toggleButtonState.remove(tag);
		}

		// ��ʂŐV�K�ɐݒ肳��Ă��^�O�̏���(DB�ɂ͑��݂��Ȃ�)
		createAddedTagEntry(m, toggleButtonState, memo);

		// Material�̏�ԍX�V�Ɖi����
		MaterialUtil.updateMaterialState(m);
		storeMaterial(m);
	}

	/**
	 * Material���i��������
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
	 * Material m�̂���chronicle�ł͂Ȃ��^�O�̐擪1���ڂ̃�����ԋp����
	 * @param m
	 * @return
	 */
	private String selectDefaultMemo(Material m) {
		// chronicle�ł͂Ȃ��擪1���̃������擾
		String defaultMemo="";
		for(TaggedMaterial tm:m.getTaggedMaterials()){
			String tag = tm.getId().getTag();
			if(!tm.getMemo().isEmpty() && !tag.equals("kazunori") && !tag.equals("hiroko") && !tag.equals("taito") ){
				defaultMemo=tm.getMemo();
				break;
			}
		}
		logger.info("�V�K�o�^�p�f�t�H���g������["+defaultMemo+"]�ł�.");
		return defaultMemo;
	}

	/**
	 * �^�O��Ԃ��X�V����(�f�V�W�����e�[�u���FMW_SceneDesign[EditMaterial�⑫(1)]���Q��)
	 * @param tm �X�V�Ώۂ�TaggedMaterial
	 * @param isSelected ��ʏ�ł��̃^�O���I������Ă��邩�ۂ�
	 * @throws MWException
	 */
	private void updateTagState(TaggedMaterial tm, Boolean isSelected){
		logger.info("�^�O["+tm.getId().getTag()+"]�̏�ԍX�V�������s���܂��B");
		// DB���̏��
		String state = tm.getTagState();

		// ��Ԕ���
		if(state.equals(Constants.TAG_STATE_STAGED)){
			if(isSelected.booleanValue()){
				logger.info("�쐬2�FTaggedMaterial��Ԃ͕ύX���܂���.");
			} else {
				logger.info("�폜1�FTaggedMaterial��Ԃ�3(Not In Use)�ɍX�V���܂�.");
				tm.setTagState(Constants.TAG_STATE_NOT_IN_USE);
			}
		} else if(state.equals(Constants.TAG_STATE_PUBLISHED)){
			if(isSelected.booleanValue()){
				logger.info("Published�^�O�̏�Ԃ͕ύX���܂���.");
			} else {
				// ��O�FBug(Published�ł�Disable���䂵�Ă���)
				throw new MWImplementationError("[BUG] TAG already Published.Cannot remove.");
			}
		} else if(state.equals(Constants.TAG_STATE_NOT_IN_USE)){
			if(isSelected.booleanValue()){
				logger.info("�쐬3�FTaggedMaterial��Ԃ�Staged�ɍX�V");
				tm.setTagState(Constants.TAG_STATE_STAGED);
			} else {
				logger.info("NotInUse�^�O�̏�Ԃ͕ύX���܂���.");
			}
		} else {
			// ��O:Bug
			throw new MWImplementationError("[BUG] Unknwon tag state["+state+"].");
		}
	}

	/**
	 * ��ʏ�Œǉ����ꂽ�^�O�G���g���ɑΉ�����TaggedMaterial��V�K�ɍ쐬���AMaterial m�Ɋ֘A�t����
	 * @param m
	 * @param toggleButtonState
	 * @param memo
	 */
	private void createAddedTagEntry(Material m, Map<String, Boolean> toggleButtonState, String memo) {
		for(Map.Entry<String, Boolean> entry:toggleButtonState.entrySet()) {
			if(entry.getValue().booleanValue()) {
				logger.fine("�^�O:"+entry.getKey()+"�͑I������Ă��܂����ADB���o�^�̂��ߐV�KTaggedMaterial���쐬���܂�.");
				// �쐬1�FTaggedMaterial���쐬�B
				TaggedMaterialPK pk = new TaggedMaterialPK();
				pk.setMaterialId(m.getMaterialId());
				pk.setTag(entry.getKey());

				TaggedMaterial taggedMaterial = new TaggedMaterial();
				taggedMaterial.setId(pk);
				taggedMaterial.setMaterial(m);
				updateMemo(taggedMaterial, memo); // �N���j�N���n�ł̓����̓��ʃ��[���݂�
				taggedMaterial.setTagState(Constants.TAG_STATE_STAGED);

				m.getTaggedMaterials().add(taggedMaterial);
			} else {
				// no action
			}
		}
	}
	
	/**
	 * �N���j�N���n�̃����ݒ胋�[�����������������ݒ��
	 * @param tm
	 * @param memo
	 * @return
	 */
	private void updateMemo(TaggedMaterial tm, String memo) {
		Material m = tm.getMaterial();
		String tag = tm.getId().getTag();

		if(tag.equals("kazunori")||tag.equals("hiroko")||tag.equals("taito")){
			logger.info("tag["+tag+"]�̂��߃N���j�N���E���[�����K�p����܂�");
			tm.setMemo(AgeCalculator.calcAge(tag, MaterialUtil.getMaterialYearMonthDate(m))+"�΂̏ё�");
		} else {
			tm.setMemo(memo);
		}
	}

}
