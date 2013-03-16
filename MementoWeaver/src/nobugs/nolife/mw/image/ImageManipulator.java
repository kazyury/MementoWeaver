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
	 * sourcePath�Ŏw�肳�ꂽ�摜�t�@�C����degree�x����������]����Bdegree��90/270�݂̂�z�肵�Ă���B
	 * @param sourcePath
	 * @param degree
	 * @throws IOException 
	 */
	public static void rotate(Path sourcePath, int degree) throws IOException{
		ImageInputStream is = new FileImageInputStream(sourcePath.toFile());
		BufferedImage sourceImage = ImageIO.read(is);

		// ���̑���ł�90�x��]��270�x��]�������Ȃ��̂ŁAheight��width�����ւ���BufferedImage���쐬���Ă���B
		BufferedImage rotatedImage = new BufferedImage(sourceImage.getHeight(), sourceImage.getWidth(), sourceImage.getType());

		AffineTransform affine = new AffineTransform();

		//	 �I���W�i���̉�(3���ԋ�J������)
		//	affine.translate(-(sourceImage.getWidth()-sourceImage.getHeight())/2, (sourceImage.getWidth()-sourceImage.getHeight())/2);
		//	affine.rotate(Math.toRadians(degree), sourceImage.getWidth()/2d, sourceImage.getHeight()/2d);

		int h = sourceImage.getHeight();
		int w = sourceImage.getWidth();
		if(degree==90){
			affine.rotate(Math.toRadians(degree), h/2, h/2);
		} else if(degree==270){
			affine.rotate(Math.toRadians(degree), w/2, w/2);
		} else {
			// TODO ��O�X���[
			System.out.println("degree should be 90 or 270.");
			return;
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
