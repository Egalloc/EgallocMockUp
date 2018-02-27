package data;

import java.awt.image.BufferedImage;

import data.ResultType.ResultType;

public class Result {
	ResultType resultType = null;
	public String keyword = null;
	public String errorMessage = null;
	public BufferedImage collageImage = null;
	
	public Result(ResultType resultType, String keyword, String errorMessage) {
		this.resultType = resultType;
		this.keyword = keyword;
		this.errorMessage = errorMessage;
	}
	
	public Result(ResultType resultType, String keyword, BufferedImage image) {
		this.resultType = resultType;
		this.keyword = keyword;
		this.collageImage = image;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public BufferedImage getCollageImage() {
		return collageImage;
	}
	
	public String getErrorMessage() {		// if success, holds imageFilePath; if failure, holds error message
		return errorMessage;
	}
	
	public boolean isSuccess() {
		return resultType == ResultType.success;
	}
	
	public boolean isFailure() {
		return resultType == ResultType.failure;
	}
}
