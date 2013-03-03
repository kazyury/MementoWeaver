package nobugs.nolife.mw.processor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import nobugs.nolife.mw.AppMain;
import nobugs.nolife.mw.derivatizer.Derivatizer;
import nobugs.nolife.mw.derivatizer.DerivatizerFactory;
import nobugs.nolife.mw.persistence.Material;
import nobugs.nolife.mw.util.Constants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class InstallMaterialProcessor extends AnchorPane implements Initializable{
	private AppMain appl;
	private Properties dirProperties = new Properties();
	private String materialSourcePath = null; // プロパティファイルより
	private String stagingAreaPath = null;    // プロパティファイルより
	private File srcdir = null; // 素材ソース
	private File todir = null;  // ステージングエリア

	// 入力フィールドに対応するインスタンスを保持する変数
	// 対応付けはFXMLファイルで定義する
	// publicフィールドの場合は@FXMLの記述は不要
	@FXML private TextField pathInput;

	// イベントハンドラ
	@FXML	protected void install(ActionEvent e) {
		EntityManager em = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME).createEntityManager();

		// 素材ソース、ステージングエリアのパスをチェック
		if (!isValidPathSet()) {
			System.out.println("checkFilePath() fails.");
			return; // TODO 例外スロー
		}

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
		File[] materialList = srcdir.listFiles(filter);
		for (File material:materialList) {
			
			// MaterialEntityの生成
			Material materialEntity = new Material();

			// ファイルタイプの取得
			int pos = material.getPath().lastIndexOf(".");
			String suffix = material.getPath().toLowerCase().substring(pos+1);
			
			if (suffix.equals("jpg")||suffix.equals("jpeg")){
				suffix = "jpg";
				materialEntity.setMaterialType(Constants.MATERIAL_TYPE_JPG);
			} else if (suffix.equals("mov")){
				materialEntity.setMaterialType(Constants.MATERIAL_TYPE_MOV);
			} else {
				System.out.println("Unsupported file type");
				return; // TODO 例外を投げるようにするべき。
			}

			// ファイルタイムスタンプの取得
			Date lastModifiedDate = new Date(material.lastModified());
			DateFormat filenameFormat = new SimpleDateFormat("yyyyMMdd'_'hhmmss");
			DateFormat materialIdFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			DateFormat yearFormat = new SimpleDateFormat("yyyy");
			DateFormat monthFormat = new SimpleDateFormat("MM");
			
			String timestamp = filenameFormat.format(lastModifiedDate);
			materialEntity.setMaterialId(materialIdFormat.format(lastModifiedDate));
			materialEntity.setCreatedYear(Integer.parseInt(yearFormat.format(lastModifiedDate)));
			materialEntity.setCreatedMonth(Integer.parseInt(monthFormat.format(lastModifiedDate)));

			// ステージングエリアにファイル名を変更してコピー
			java.nio.file.Path dest = new File(todir,timestamp+"."+suffix).toPath();
			try {
				Files.copy(material.toPath(), dest, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e1) {
				//TODO ここは握りつぶさないほうがいいかな。
				e1.printStackTrace();
				return;
			}
			// DerivationManager(Derivatizer)に派生ファイルの作成を要求
			Derivatizer derivatizer = DerivatizerFactory.getDerivatizer(material);
			derivatizer.derivate();
			
			//　PersistenceManagerにインストール状況の登録を要求
			materialEntity.setMaterialState(Constants.MATERIAL_STATE_INSTALLED);
			em.getTransaction().begin();
			em.persist(materialEntity);
			em.getTransaction().commit();
		}
		em.close();
		//TODO MaterialSourceManagerにキャッシュを要求
		//TODO 次の画面への遷移
	}

	@FXML	protected void browse(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose digital camera photo path.");
		File file = directoryChooser.showDialog(null);
		if (file!=null) {
			pathInput.setText(file.getPath());
		}
	}

	@FXML	protected void exit(ActionEvent e) {} // TODO not implemented yet

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// プロパティの読み込み
		InputStream is = this.getClass().getResourceAsStream("dir.properties");
		try {
			dirProperties.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		materialSourcePath = dirProperties.getProperty("dir.materialSource");
		stagingAreaPath = dirProperties.getProperty("dir.stagingArea");

		// プロパティに指定された素材ソースを初期設定
		pathInput.setText(materialSourcePath);
	}

	public void setApplication(AppMain appMain) {
		appl = appMain;
	}

	private boolean isValidPathSet() {
		// 素材ソース(pathInput)を取得
		String src = pathInput.getText();

		// pathInputが空若しくは存在しないパスならばエラーメッセージを表示してreturn
		srcdir = new File(src);
		if (!srcdir.isDirectory()) {
			//TODO エラーメッセージの表示 javaFX1.3 では javafx.stage.Alertが有ったが2.2ではなくなっている。3で復活の予定らしいが。
			System.out.println(src + " is not Directory.");
			return false;
		}

		// ステージングエリアが空若しくは存在しないパスならばエラーメッセージを表示してreturn
		todir = new File(stagingAreaPath);
		if (!todir.isDirectory()) {
			//TODO エラーメッセージの表示 javaFX1.3 では javafx.stage.Alertが有ったが2.2ではなくなっている。3で復活の予定らしいが。
			System.out.println(stagingAreaPath + " is not Directory.");
			return false;
		}
		return  true;
	}
}
