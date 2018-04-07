package servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletEngine() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Check if request is for keyword */
		// get keyword parameter from HttpServletRequest
		String keyword = getKeywordFromRequest(request);
		if (keyword != null) {
			Result result = sendKeywordToServer(keyword);
			setSessionAttributes(request, result);
			Timestamp finishTime = new Timestamp(System.currentTimeMillis());
			System.out.println("Request Finished " + finishTime);
			return;	// Completed request, return
		}

		/* Check if request is for viewing a saved collage */
		// get saved collage id parameter from HttpServletRequest
		String scIDString = getSavedCollageIDFromRequest(request);
		if (scIDString != null) {
			Integer scID = Integer.parseInt(scIDString);
			viewSavedCollage(request, scID);
			return; // Completed request, return
		}

		return; // dummy return variable
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

	//	Returns value for KEYWORD_PARAMETER in HttpServletRequest
	private String getKeywordFromRequest(HttpServletRequest request) {
		return request.getParameter(Constants.KEYWORD_PARAMETER);
	}

	// Returns value for SAVED_COLLAGE_ID_PARAMETER in HttpServletRequest
	private String getSavedCollageIDFromRequest(HttpServletRequest request) {
		return request.getParameter(Constants.SAVED_COLLAGE_ID_PARAMETER);
	}

	//	Sends the keyword to the Server and returns the Result received from Server
	private Result sendKeywordToServer(String keyword) {
		return Server.getInstance().getResultForKeyword(keyword);
	}

	//	Sets Result as currently displayed result
	private void setSessionAttributes(HttpServletRequest request, Result result) {
		HttpSession session = request.getSession();
		Result currentResult  = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);

		//	if currently displaying a collage, then save the collage before
		//		setting the current collage to a new one
		if (currentResult != null && currentResult.isSuccess()) {
			saveCurrentCollage(session);
		}

		// saves new result as SESSION_CURRENT_RESULT
		session.setAttribute(Constants.SESSION_CURRENT_RESULT, result);
	}

	//	If collage is currently being displayed, saves collage
	//		to savedCollages
	private void saveCurrentCollage(HttpSession session) {
		@SuppressWarnings("unchecked")
		List<Result> savedCollages = (List<Result>) session.getAttribute(Constants.SESSION_SAVED_COLLAGES);

		// if no collages have been saved, initialize list of Results
		if (savedCollages == null) {
			savedCollages = new ArrayList<Result>();
		}

		Result currentResult = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
		savedCollages.add(currentResult);	// add current collage to savedCollages

		// saves savedCollages as SESSION_SAVED_COLLAGES
		session.setAttribute(Constants.SESSION_SAVED_COLLAGES, savedCollages);
	}

	//	Switches currently displayed result with clicked saved collage
	private void viewSavedCollage(HttpServletRequest request, Integer scID) {
		HttpSession session = request.getSession();
		Result currentResult  = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);

		@SuppressWarnings("unchecked")
		List<Result> savedCollages = (List<Result>) session.getAttribute(Constants.SESSION_SAVED_COLLAGES);
		Result collageToDisplay = null;

		// error is currently being displayed
		if (currentResult.isFailure()) {
			collageToDisplay = savedCollages.get(scID);
			// set collageToDisplay to saved-collage

		//	collage is being displayed
		} else {
			// replace clicked saved-collage with current collage in savedCollages
			// set collageToDisplay to saved-collage
			collageToDisplay = savedCollages.set(scID, currentResult);
		}

		// saves collageToDisplay as SESSION_CURRENT_RESULT
		session.setAttribute(Constants.SESSION_CURRENT_RESULT, collageToDisplay);
	}

}
