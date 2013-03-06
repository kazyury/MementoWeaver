package nobugs.nolife.mw.derivatizer;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import nobugs.nolife.mw.util.Constants;

public abstract class Derivatizer {
	public abstract void derivate();

	protected void createThumbnail(File path){
		createThumbnail(path.toPath());
	}
	protected void createThumbnail(Path path){
		System.out.println(path.toString()+"�̃T���l�C���쐬���J�n���܂��B");
		
		BufferedImage sourceImage = null;
		BufferedImage thumbnailImage = null;
		FileOutputStream out;

		try {
			// �o�̓t�@�C���p�X
			File thumbnailFile = getOutputFile(path);

			sourceImage = ImageIO.read(path.toFile());

			// �䗦�v�Z
			int width = sourceImage.getWidth();
			int height = sourceImage.getHeight();
			double ratio = calcRatio(width, height);

			// �T���l�C���ϊ�
			thumbnailImage = new BufferedImage((int)(width*ratio), (int)(height*ratio), sourceImage.getType());
			AffineTransformOp operator = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			operator.filter(sourceImage, thumbnailImage);

			// �T���l�C���̏����o��
			out = new FileOutputStream(thumbnailFile);
			ImageIO.write(thumbnailImage, "jpeg", out);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private double calcRatio(int width, int height) {
		// �T���l�C���͒��ӂ�240pix�ƂȂ�悤�k������B
		double ratio = 0.0;
		if (width > height) {
			ratio = Constants.THUMBNAIL_LONG_SIDE_PIXELS/width;
		} else {
			ratio = Constants.THUMBNAIL_LONG_SIDE_PIXELS/height;
		}
		return ratio;
	}

	private File getOutputFile(Path path) throws IOException{
		FileSystem fs = FileSystems.getDefault();
		Path parent = path.getParent();
		Path basename = path.getFileName();

		Path thumbnailDirectory = fs.getPath(parent.toString(), Constants.THUMBNAIL_SUBPATH);
		if (Files.notExists(thumbnailDirectory, LinkOption.NOFOLLOW_LINKS)) {
			Files.createDirectories(thumbnailDirectory);
		}

		String outfilename = fs.getPath(thumbnailDirectory.toString(), basename.toString()).toString();
		return new File(outfilename);
	}
}
