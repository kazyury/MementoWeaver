package nobugs.nolife.mw.derivatizer;

import java.io.File;

public class QuicktimeDerivatizer extends Derivatizer {
	private File path;
	
	public QuicktimeDerivatizer(File path){
		this.path = path;
	}

	@Override
	public void derivate() {
		// TODO 動画素材からの派生ファイルの作成を行う
		// TODO 動画スナップショットの作成
		// TODO 動画スナップショットのサムネイル(動画サムネイル)の作成
	}

}
