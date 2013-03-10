package nobugs.nolife.mw.util;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import nobugs.nolife.mw.image.ImageManipulator;
import nobugs.nolife.mw.persistence.Material;

public class MaterialUtil {

	public static String getBaseFileName(Material m) {
		String materialId = m.getMaterialId();
		StringBuffer materialName = new StringBuffer();
		materialName.append(materialId.substring(0, 8));
		materialName.append("_");
		materialName.append(materialId.substring(8));
		return materialName.toString();
	}

	public static String getFileName(Material m) {
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			materialName.append(".jpg");
		} else if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			materialName.append(".mov");
		} else {
			System.out.println("�f�ރ^�C�v�s��");
		}
		return materialName.toString();
	}

	/**
	 * Material�̐Î~�摜�̃t�@�C�����𕶎���Ƃ��ĕԋp����B
	 * (�Î~��f�ނ̏ꍇ��getFileName�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t�@�C����)
	 * @param m
	 * @return
	 */
	public static String getPhotoFileName(Material m) {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getFileName(m);
		}
		StringBuffer materialName = new StringBuffer(getBaseFileName(m));
		materialName.insert(0, "ff");
		materialName.append(".jpg");
		return materialName.toString();
	}
	
	/**
	 * Material�̃t���p�X��ԋp����B
	 * @param m
	 * @return
	 */
	public static Path getInstalledFilePath(Material m) {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(), getFileName(m));
	}

	/**
	 * Material�̐Î~�摜�̃t���p�X��ԋp����B
	 * (�Î~��f�ނ̏ꍇ��getInstalledFilePath�Ɠ��l.����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃t���p�X)
	 * @param m
	 * @return
	 */
	public static Path getInstalledPhotoPath(Material m) {
		if (m.getMaterialType().equals(Constants.MATERIAL_TYPE_JPG)) {
			return getInstalledFilePath(m);
		}
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(),getPhotoFileName(m));
	}
	
	/**
	 * Material�̃T���l�C���̃t���p�X��ԋp����B
	 * ����f�ނ̏ꍇ�̓X�i�b�v�V���b�g�̃T���l�C���̃p�X��ԋp����B
	 * @param m
	 * @return
	 */
	public static Path getInstalledThumbnailPath(Material m) {
		PropertyUtil prop = new PropertyUtil();
		FileSystem fs = FileSystems.getDefault();
		return fs.getPath(prop.getStagingAreaName(),"thumbnail",getPhotoFileName(m));
	}

	/** 
	 * �摜����]���ĕۊǂ���B�Î~��f�ޖ{�̂ƃT���l�C�����ΏۂƂȂ�B
	 * @param m
	 * @param degree:��]�p
	 */
	public static void rotatePhoto(Material m,int degree){
		if(m.getMaterialType().equals(Constants.MATERIAL_TYPE_MOV)) {
			System.out.println("����f�ނ͉�]�ɑΉ����Ă��܂���"); // TODO ��O���o
			return;
		}

		try {
			// �f�ޖ{�̂���]����B
			ImageManipulator.rotate(getInstalledPhotoPath(m), degree);
			// �T���l�C������]����B
			ImageManipulator.rotate(getInstalledThumbnailPath(m), degree);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
