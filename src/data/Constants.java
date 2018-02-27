package data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.ResultType.ResultType;

public class Constants {
	public static final String KEYWORD_PARAMETER = "keyword";
	public static final String SAVED_COLLAGE_ID_PARAMETER = "sc-id";
	public static final String ERROR_MESSAGE = "Insufficient number of images found";
	
	public static final String SESSION_ERROR = "isError";
	public static final String SESSION_SAVED_COLLAGES = "savedCollages";
	public static final String SESSION_CURRENT_RESULT = "currentCollage";
	
	public static final String BUILD_ANOTHER_COLLAGE = "Build Another Collage";
	
	public static String getImage(BufferedImage bImage) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bImage, "jpg", baos );
			baos.flush();
			byte[] imageInByteArray = baos.toByteArray();
			baos.close();
			String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);
			return b64;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static final int COLLAGE_WIDTH = 2000;
	public static final int COLLAGE_HEIGHT = 1500;
	
	
	/* delete */
	
	public static BufferedImage getBIForFilePath(String filePath) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return dropAlphaChannel(img);
	}
	
	private static BufferedImage dropAlphaChannel(BufferedImage src) {
	     BufferedImage convertedImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
	     convertedImg.getGraphics().drawImage(src, 0, 0, null);

	     return convertedImg;
	}
	
	public static Result createCatResult() {
		String filePath = "/Users/Joann/csci201/Egalloc/WebContent/img/cat-collage.png";
		Result catResult = new Result(ResultType.success, "Cat", getBIForFilePath(filePath));
		return catResult;
	}
	
	public static Result createDogResult() {
		String filePath = "/Users/Joann/csci201/Egalloc/WebContent/img/dog-collage.png";
		Result catResult = new Result(ResultType.success, "Dog", getBIForFilePath(filePath));
		return catResult;
	}

}
