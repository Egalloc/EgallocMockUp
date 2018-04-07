<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
%>		
<%@page
	import="data.Result"
	import="java.util.*"
	import="data.Constants"
	import="java.awt.image.BufferedImage"
%>

<!doctype html>
<html
	class="no-js"
	lang=""
>
<head>
<meta charset="utf-8">
<meta
	http-equiv="x-ua-compatible"
	content="ie=edge"
>
<title></title>
<meta
	name="description"
	content=""
>
<meta
	name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no"
>

<link
	rel="manifest"
	href="site.webmanifest"
>
<link
	rel="apple-touch-icon"
	href="icon.png"
>
<!-- Place favicon.ico in the root directory -->

<link
	rel="stylesheet"
	href="css/main.css"
>
<link
	rel="stylesheet"
	href="css/secondpage.css"
>
<script
	src="js/buildCollage.js"
	type="text/javascript"
></script>
<script type="text/javascript">
		function savedCollageClicked(scID) {
			var xhttp = new XMLHttpRequest();
			xhttp.open("GET", "ServletEngine?sc-id=" + scID, true);
			xhttp.onload = function (e) {
				if (xhttp.readyState === 4) {
					if (xhttp.status === 200) {
						redirectToSecondPage(xhttp.responseText);
					} else {
						console.error("errorr: " + xhttp.responseText);
					}
				}
			};
			xhttp.onerror = function (e) {
				console.error(xhttp.statusText);
			};
			xhttp.send();
		}
		
		function exportCollage() {
			
			var img = document.getElementById("collage");
			if (img == null) { return; }
			var fileName = img.name;
			var a = document.createElement('a');
			a.href = img.src;
			a.download = fileName + '.png';
			a.style = 'display: none';
			img.parentNode.appendChild(a);
			a.click();
			a.remove();
			
			return false;
		}
	</script>
</head>
<body>
	<!-- Add your site or application content here -->
	<div id="content">

		<div id="collage-generator">
			<div id="collage-container">
				<div
					id="collage-caption"
					class="display-collage centered-width"
				>
					<%
						Result currentResult = (Result) session.getAttribute(Constants.SESSION_CURRENT_RESULT);
						String caption = "";
						if (currentResult == null) {
							System.out.println("current result is null");
						}
						if (currentResult.isSuccess()) {
							caption = "Collage for " + currentResult.getKeyword();
						}
					%>
					<%=caption%>
				</div>
				<div
					id="collage-area"
					class="collage"
				>
					<%
						if (currentResult.isSuccess()) {
							BufferedImage bImage = currentResult.getCollageImage();
							out.print("<img id=\"collage\"" 
							+ "src=\"data:image/jpg;base64, " 
							+ Constants.getImage(bImage) 
							+ "\" alt=\"img not found\""
							+ "name=\"" + currentResult.getKeyword() + "\""
							+ "class=\"display-collage collage-area\" />");
						} else {
							out.print("<div id=\"collage-placeholder\" class=\"collage-area centered-width\">"
									+ "<div class=\"error-message\">" + currentResult.getErrorMessage() + "</div></div>");
						}
					%>
				</div>
			</div>
			<div id="search-container">
				<div id="search-bar-container">
					<input
						id="search-bar-input"
						type="text"
						placeholder="Enter topic"
					/>
					<button
						id="search-bar-submit"
						class="search-bar-button"
						type="submit"
						onclick="checkForText()"
					><%=Constants.BUILD_ANOTHER_COLLAGE%></button>
					<button
						id="search-bar-export"
						class="search-bar-button"
						type="submit"
						onclick="exportCollage()"
					>Export Collage</button>
				</div>
			</div>
		</div>
		<div id="saved-collages">
			<%
				List<Result> savedCollages = (List<Result>) session.getAttribute("savedCollages");
				if (savedCollages == null || savedCollages.isEmpty()) { 
					return;
				}
				for (int i = 0; i < savedCollages.size(); i++) {
					BufferedImage bImage = savedCollages.get(i).getCollageImage();
					out.print("<div id=\"" + i + "\" class=\"saved-collage\" onClick=\"savedCollageClicked(this.id)\"><img src=\"data:image/jpg;base64, " +  Constants.getImage(bImage) + "\"class=\"saved-collage\"/></div>");
				}
			%>
		</div>
	</div>
	<script src="js/main.js"></script>
    <script>
    	document.querySelector("#search-bar-input").addEventListener("keyup", event => {
	      if(event.key !== "Enter") return; // Use `.key` instead.
	      document.querySelector("#search-bar-submit").click(); // Things you want to do.
	      event.preventDefault(); // No need to `return false;`.
  		});
   	</script>
</body>
</html>
