package data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.ResultType.ResultType;

public class Constants {
	/* REQUEST PARAMETERS */
	public static final String KEYWORD_PARAMETER = "keyword";
	public static final String SAVED_COLLAGE_ID_PARAMETER = "sc-id";

	/* SESSION VARIABLES */
	public static final String SESSION_ERROR = "isError";
	public static final String SESSION_SAVED_COLLAGES = "savedCollages";
	public static final String SESSION_CURRENT_RESULT = "currentCollage";

	/* JSP */
	public static final String BUILD_COLLAGE = "Build Collage";
	public static final String BUILD_ANOTHER_COLLAGE = "Build Another Collage";
	public static final String EXPORT_COLLAGE = "Export Collage";

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

	/* COLLAGE/ERROR VARIABLES */
	public static final int COLLAGE_WIDTH = 2000;
	public static final int COLLAGE_HEIGHT = 1500;
	public static final String ERROR_MESSAGE = "Insufficient number of images found";

	/* API COMMUNICATOR */
    // Static constants
    public static final int API_IMAGE_NUMBER = 30;
    public static final int API_REQUEST_THREAD_NUMBER = 5;
    public static final int URL_PROCESS_THREAD_NUMBER = 35;
    public static final int URL_TIMEOUT_LIMIT = 5000;
    public static final int THREAD_NUMBER = 40;
    public static final int SLEEP_TIME = 1000;
    public static final int ELEMENT_PER_REQUEST = 10;
    public static final int SLEEP_TIME_OUT = 15;

    // The constants below are for the request
    public static final String URL_REQUEST_PROPERTY_KEY = "User-Agent";
    public static final String URL_REQUEST_PROPERTY_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " +
            "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
    public static final String REQUEST_URL = "https://www.googleapis.com/customsearch/v1";
    //public static final String API_KEY = "AIzaSyBjefw6KjolHODgNI2IZhvYh7jzoSzG44A";	// alternative developer key
    public static final String API_KEY = "AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw";
    public static final String CX = "003668417098658282383:2ym3vezfm44";
    //public static final String CX = "010274188002515510907:oecl9salb6i";	// alternative developer key
    public static final String SEARCH_TYPE = "image";
    public static final String API_REQUEST_PROPERTY_KEY = "Content-Type";
    public static final String API_REQUEST_PROPERTY_VALUE = "application/json";

  /* COLLAGE BUILDER */
  public static final int CB_IMAGE_NUMBER = 30;
  public static final double TOTAL_AREA_RATIO = 1.5;
  public static final int BORDER_PIXEL = 3;
  public static final int PADDING_X = 100;
  public static final int PADDING_Y = 50;
  public static final int MAXIMUM_ANGLE = 45;
  public static final int MINIMUM_ANGLE = -45;
  public static final int MINIMUM_ANGLE_FOR_FIRST_IMAGE = 5;
  public static final int NUMBER_OF_ROW = 3;
  public static final int IMAGE_PER_ROW = 10;
  public static final int SECOND_ROW_INDEX = IMAGE_PER_ROW + 1;
  public static final int THIRD_ROW_INDEX = IMAGE_PER_ROW * 2 + 1;
}
