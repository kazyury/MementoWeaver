package nobugs.nolife.mw.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWImplementationError;
import nobugs.nolife.mw.exceptions.MWResourceIOError;

/**
 * 素材画像の操作に責務を持つ
 * @author kazyury
 *
 */
public class ImageManipulator {
	private static Logger logger = Logger.getGlobal();

	/**
	 * sourcePathで指定された画像ファイルをdegree度数分だけ回転する。degreeは90/270のみを想定している。
	 * @param sourcePath
	 * @param degree
	 * @throws IOException 
	 * @throws MWException 
	 */
	public static void rotate(Path sourcePath, int degree) {
		logger.fine(sourcePath.toString()+"の回転処理を開始");
		ImageInputStream is=null;
		BufferedImage sourceImage = null;
		try {
			is = new FileImageInputStream(sourcePath.toFile());
			sourceImage = ImageIO.read(is);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}

		// 一回の操作では90度回転か270度回転かしかないので、heightとwidthを入れ替えてBufferedImageを作成している。
		BufferedImage rotatedImage = new BufferedImage(sourceImage.getHeight(), sourceImage.getWidth(), sourceImage.getType());

		AffineTransform affine = new AffineTransform();

		int h = sourceImage.getHeight();
		int w = sourceImage.getWidth();
		if(degree==90){
			logger.fine("90度の回転実行");
			affine.rotate(Math.toRadians(degree), h/2, h/2);
		} else if(degree==270){
			logger.fine("270度の回転実行");
			affine.rotate(Math.toRadians(degree), w/2, w/2);
		} else {
			throw new MWImplementationError("degree should be 90 or 270.");
		}

		AffineTransformOp operator = new AffineTransformOp(affine,AffineTransformOp.TYPE_BICUBIC);
		operator.filter(sourceImage, rotatedImage);

		// 書き出し
		Path tmpfile=null;
		try {
			tmpfile = Files.createTempFile("mwImageManipulator", ".tmp");
		} catch (IOException e) {
			throw new MWResourceIOError("tmpファイルの作成時に例外が発生しました.",e);
		}

		try(FileOutputStream os = new FileOutputStream(tmpfile.toFile())) {
			ImageIO.write(rotatedImage, "jpeg", os);
			Files.move(tmpfile, sourcePath,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
	}
}
