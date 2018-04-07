package Server;

import java.util.*;
import java.sql.Timestamp;

import data.Constants;
import data.Result;
import data.ResultType.ResultType;
import java.awt.image.BufferedImage;

public class Server {

    private static Server server_instance;

    private Server() {}

    static{
    	server_instance = new Server();
    }

    public static Server getInstance(){
        return server_instance;
    }
	
	private Map<String,Result> savedResults = new HashMap<String,Result>();
	
	public Result getResultForKeyword(String keyword) {
		Result result = null;
		if (checkIfResultExistsForKeyword(keyword)) {
			result = retrieveResultForKeyword(keyword);
		} else {
			result = createResultForResponse(keyword);
		}
		saveResultForKeyword(keyword, result);
		return result;
	}
	
	//saveResultForKeyword
	private void saveResultForKeyword(String keyword, Result result) {
			savedResults.put(keyword, result);
		}
	
	//checkIfResultExistsForKeyword
	private boolean checkIfResultExistsForKeyword(String keyword) {
		return savedResults.containsKey(keyword);
	}
	//retriveResultForKeyword
	private Result retrieveResultForKeyword(String keyword) {
		return savedResults.get(keyword);
	}
	
	//createResultForResponse
	private Result createResultForResponse(String keyword) {
		Timestamp requestTime = new Timestamp(System.currentTimeMillis());
		System.out.println(keyword + " requested: " + requestTime);
		APICommunicator comm = new APICommunicator(keyword);
		//check sufficient
		List<BufferedImage> images = comm.getImages();
		boolean suff = false;
		if (images.size() == 30) {
			suff = true;
		}
		if (suff) {
		//send to collage builder
			CollageBuilder cb = new CollageBuilder(images);
			BufferedImage resultImage = cb.createCollageWithImages(Constants.COLLAGE_WIDTH,Constants.COLLAGE_HEIGHT);
			return new Result(ResultType.success, keyword, resultImage);
		}
		else {
			return new Result(ResultType.failure, keyword, Constants.ERROR_MESSAGE);
		 }
		 
	}
	
}