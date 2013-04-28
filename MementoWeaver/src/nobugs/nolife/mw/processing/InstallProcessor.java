package nobugs.nolife.mw.processing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.derivatizer.Derivatizer;
import nobugs.nolife.mw.derivatizer.DerivatizerFactory;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.exceptions.MWConfigurationError;
import nobugs.nolife.mw.exceptions.MWInvalidUserInputException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class InstallProcessor {

	private File sourceDirectory;
	private File targetDirectory;
	
	public void installProcess(String srcPath, String destPath) throws MWInvalidUserInputException{
		sourceDirectory = new File(srcPath);
		targetDirectory = new File(destPath);
		
		EntityManager em = PersistenceUtil.getMWEntityManager();

		// 素材ソース、ステージングエリアのパスをチェック
		isValidPathSet();

		// ファイルリストの取得。.jpeg, .jpg, .mov を対象とする(大文字小文字は無視)
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dirname, String filename) {
				filename = filename.toLowerCase();
				if ( filename.endsWith(".jpeg") || filename.endsWith(".jpg") || filename.endsWith(".mov") ) {
					return true;
				} 
				return false;
			}
		};
		
		// 素材ソース内のファイル毎の繰り返し
		File[] materialList = sourceDirectory.listFiles(filter);
		for (File material:materialList) {
			
			// MaterialEntityの生成
			Material materialEntity = new Material();

			// ファイルタイプの取得と設定
			materialEntity.setMaterialType(MaterialUtil.getMaterialType(material.toPath()));
			String suffix = MaterialUtil.getNormalizedSuffix(material);

			// ファイルタイムスタンプの取得と設定
			Date lastModifiedDate = new Date(material.lastModified());
			
			String destBaseFilename = formatDate(lastModifiedDate, "yyyyMMdd'_'hhmmss");
			materialEntity.setMaterialId(formatDate(lastModifiedDate,"yyyyMMddhhmmss"));
			materialEntity.setCreatedYear(Integer.parseInt(formatDate(lastModifiedDate,"yyyy")));
			materialEntity.setCreatedMonth(Integer.parseInt(formatDate(lastModifiedDate,"MM")));
			
			// ステージングエリアにファイル名を変更してコピー
			Path dest = new File(targetDirectory,destBaseFilename+"."+suffix).toPath();
			try {
				Files.copy(material.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e1) {
				throw new MWResourceIOError("ステージングエリアへのファイルコピーで例外が発生しました", e1.getCause());
			}
			// DerivationManager(Derivatizer)に派生ファイルの作成を要求
			Derivatizer derivatizer = DerivatizerFactory.getDerivatizer(dest);
			derivatizer.derivate();
			
			//　PersistenceManagerにインストール状況の登録を要求
			materialEntity.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
			em.getTransaction().begin();
			em.persist(materialEntity);
			em.getTransaction().commit();
		}
		em.close();


		
	}
	private boolean isValidPathSet() throws MWInvalidUserInputException {

		// pathInput/ステージングエリアが空若しくは存在しないパスならば例外
		if (!sourceDirectory.isDirectory()) {
			throw new MWInvalidUserInputException(sourceDirectory.toString() + " はディレクトリではありません.");
		}
		if (!targetDirectory.isDirectory()) {
			throw new MWConfigurationError(targetDirectory.toString() + " はディレクトリではありません.");
		}
		
		return  true;
	}


	private String formatDate(Date date, String pattern) {
		DateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}
}
