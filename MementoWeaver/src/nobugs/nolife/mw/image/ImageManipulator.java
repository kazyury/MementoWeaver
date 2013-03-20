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

import nobugs.nolife.mw.MWException;

/**
 * �f�މ摜�̑���ɐӖ�������
 * @author kazyury
 *
 */
public class ImageManipulator {
	private static Logger logger = Logger.getGlobal();

	/**
	 * sourcePath�Ŏw�肳�ꂽ�摜�t�@�C����degree�x����������]����Bdegree��90/270�݂̂�z�肵�Ă���B
	 * @param sourcePath
	 * @param degree
	 * @throws IOException 
	 * @throws MWException 
	 */
	public static void rotate(Path sourcePath, int degree) throws IOException, MWException{
		logger.fine(sourcePath.toString()+"�̉�]�������J�n");
		ImageInputStream is = new FileImageInputStream(sourcePath.toFile());
		BufferedImage sourceImage = ImageIO.read(is);

		// ���̑���ł�90�x��]��270�x��]�������Ȃ��̂ŁAheight��width�����ւ���BufferedImage���쐬���Ă���B
		BufferedImage rotatedImage = new BufferedImage(sourceImage.getHeight(), sourceImage.getWidth(), sourceImage.getType());

		AffineTransform affine = new AffineTransform();

		int h = sourceImage.getHeight();
		int w = sourceImage.getWidth();
		if(degree==90){
			logger.fine("90�x�̉�]���s");
			affine.rotate(Math.toRadians(degree), h/2, h/2);
		} else if(degree==270){
			logger.fine("270�x�̉�]���s");
			affine.rotate(Math.toRadians(degree), w/2, w/2);
		} else {
			throw new MWException("degree should be 90 or 270.");
		}

		AffineTransformOp operator = new AffineTransformOp(affine,AffineTransformOp.TYPE_BICUBIC);
		operator.filter(sourceImage, rotatedImage);

		// �����o��
		Path tmpfile = Files.createTempFile("mwImageManipulator", ".tmp");
		FileOutputStream os = new FileOutputStream(tmpfile.toFile());
		ImageIO.write(rotatedImage, "jpeg", os);
		os.close();
		Files.move(tmpfile, sourcePath,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
	}
}