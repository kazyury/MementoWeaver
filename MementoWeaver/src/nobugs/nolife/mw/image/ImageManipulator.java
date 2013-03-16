package nobugs.nolife.mw.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class ImageManipulator {
	/**
	 * sourcePathで指定された画像ファイルをdegree度数分だけ回転する。degreeは90/270のみを想定している。
	 * @param sourcePath
	 * @param degree
	 * @throws IOException 
	 */
	public static void rotate(Path sourcePath, int degree) throws IOException{
		ImageInputStream is = new FileImageInputStream(sourcePath.toFile());
		BufferedImage sourceImage = ImageIO.read(is);

		// 一回の操作では90度回転か270度回転かしかないので、heightとwidthを入れ替えてBufferedImageを作成している。
		BufferedImage rotatedImage = new BufferedImage(sourceImage.getHeight(), sourceImage.getWidth(), sourceImage.getType());

		AffineTransform affine = new AffineTransform();

		//	 オリジナルの解(3時間苦労した版)
		//	affine.translate(-(sourceImage.getWidth()-sourceImage.getHeight())/2, (sourceImage.getWidth()-sourceImage.getHeight())/2);
		//	affine.rotate(Math.toRadians(degree), sourceImage.getWidth()/2d, sourceImage.getHeight()/2d);

		int h = sourceImage.getHeight();
		int w = sourceImage.getWidth();
		if(degree==90){
			affine.rotate(Math.toRadians(degree), h/2, h/2);
		} else if(degree==270){
			affine.rotate(Math.toRadians(degree), w/2, w/2);
		} else {
			// TODO 例外スロー
			System.out.println("degree should be 90 or 270.");
			return;
		}

		AffineTransformOp operator = new AffineTransformOp(affine,AffineTransformOp.TYPE_BICUBIC);
		operator.filter(sourceImage, rotatedImage);

		// 書き出し
		Path tmpfile = Files.createTempFile("mwImageManipulator", ".tmp");
		FileOutputStream os = new FileOutputStream(tmpfile.toFile());
		ImageIO.write(rotatedImage, "jpeg", os);
		os.close();
		Files.move(tmpfile, sourcePath,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
	}
}
