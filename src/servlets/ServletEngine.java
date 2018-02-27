package servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Server.Server;
import data.Constants;
import data.Result;
import data.ResultType.ResultType;

/**
 * Servlet implementation class ServletEngine
 */
@WebServlet("/ServletEngine")
public class ServletEngine extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Result tempErrorResult = new Result(
			ResultType.failure, 
			"Lion", Constants.ERROR_MESSAGE
			);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletEngine() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		
		// Check if request is for keyword
		String keyword = getKeywordFromRequest(request);	
		if (keyword != null) {
			Result result = sendKeywordToServer(keyword); 
			setSessionAttributes(request, result);
			System.out.println("return to js");
			return;
		}

		// Check if request is for viewing a saved collage
		String scIDString = getSavedCollageIDFromRequest(request); 
		if (scIDString != null) {
			Integer scID = Integer.parseInt(scIDString);	
			viewSavedCollage(request, scID);
			return;
		}
		
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * Private Functions
	 */
	private String getKeywordFromRequest(HttpServletRequest request) {
		return request.getParameter(Constants.KEYWORD_PARAMETER);
	}
	
	private String getSavedCollageIDFromRequest(HttpServletRequest request) {
		return request.getParameter(Constants.SAVED_COLLAGE_ID_PARAMETER);
	}
	
	private Result sendKeywordToServer(String keyword) {
		Result result = Server.getInstance().getResultForKeyword(keyword);
				
//		int n = new Random().nextInt(3) + 1;
//		System.out.println("returning result for n = " + n);
//		switch (n) {
//		case 1:
//			return createDogResult();
//		case 2:
//			return createCatResult();
//		case 3:
//			return tempErrorResult;
//		default:
//			return createDogResult();	
//		}
		
		return result;
	}
	
	private void setSessionAttributes(HttpServletRequest request, Result result) {
		HttpSession session = request.getSession();		
		Result currentResult  = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		
		if (currentResult != null && currentResult.isSuccess()) {
			saveCurrentCollage(session);
		}
			
		session.setAttribute(Constants.SESSION_CURRENT_RESULT, result);
	}
	
	private void saveCurrentCollage(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Result> savedCollages = (List<Result>) session.getAttribute(Constants.SESSION_SAVED_COLLAGES);
		if (savedCollages == null) {
			savedCollages = new ArrayList<Result>();
		}
		Result currentResult = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		if (currentResult.isSuccess()) {
			savedCollages.add(currentResult);
			session.setAttribute(Constants.SESSION_SAVED_COLLAGES, savedCollages);
		}
	}
	
	private void viewSavedCollage(HttpServletRequest request, Integer scID) {
		HttpSession session = request.getSession();		
		Result currentResult  = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		
		@SuppressWarnings("unchecked")
		List<Result> savedCollages = (List<Result>) session.getAttribute(Constants.SESSION_SAVED_COLLAGES);
		Result collageToDisplay = null;

		if (currentResult.isFailure()) {
			collageToDisplay = savedCollages.get(scID);
		} else {
			collageToDisplay = savedCollages.set(scID, currentResult);
		}
		
		session.setAttribute(Constants.SESSION_CURRENT_RESULT, collageToDisplay);
	}

}
