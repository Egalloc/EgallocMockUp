package data;

import java.awt.image.BufferedImage;

import data.ResultType.ResultType;

public class Result {
	/* PRIVATE VARIABLE DECLARATIONS */
	private ResultType resultType = null;
	private String keyword = null;
	private String errorMessage = null;
	private BufferedImage collageImage = null;

	/* CONSTRUCTORS */
	// Used for Failures/Errors
	public Result(ResultType resultType, String keyword, String errorMessage) {
		this.resultType = resultType;
		this.keyword = keyword;
		this.errorMessage = errorMessage;
	}

	// Used for Successes/Collages
	public Result(ResultType resultType, String keyword, BufferedImage image) {
		this.resultType = resultType;
		this.keyword = keyword;
		this.collageImage = image;
	}

	/* GETTERS */
	public String getKeyword() {
		return keyword;
	}

	public boolean isSuccess() {
		return resultType == ResultType.success;
	}

	public boolean isFailure() {
		return resultType == ResultType.failure;
	}
	
	public BufferedImage getCollageImage() {
		return collageImage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
