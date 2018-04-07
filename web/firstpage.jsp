<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
%>
<%@page
	import="data.Constants"
%>

<!doctype html>
<html class="no-js" lang="">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="manifest" href="site.webmanifest">

				<%-- CSS --%>
        <link rel="stylesheet" href="css/firstpage.css">
				<link rel="stylesheet" href="css/main.css">

				<%-- JavaScript --%>
       	<script src="js/buildCollage.js" type="text/javascript"></script>
    </head>
    <body>
        <div id="content">
            <div id="search-bar-container">
              <input id="search-bar-input" name="input-text" type="text" placeholder="Enter topic" />
              <button id="search-bar-submit" class="search-bar-button" type="submit" onclick="checkForText()"> <%=Constants.BUILD_COLLAGE%> </button>
            </div>
        </div>

				<%-- JavaScript --%>
        <script>
					// Implements "Press Enter to trigger 'Build Collage' button functionality"
	        document.querySelector("#search-bar-input").addEventListener("keyup", event => {
	          if(event.key !== "Enter") return;
	          document.querySelector("#search-bar-submit").click();
	          event.preventDefault();
	      	});
        </script>
    </body>
</html>
