<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
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
        <link rel="apple-touch-icon" href="icon.png">
        <!-- Place favicon.ico in the root directory -->

        <link rel="stylesheet" href="css/firstpage.css">
       	<script src="js/buildcollage.js" type="text/javascript"></script> 
       	<script type="text/javascript">
       	</script>
    </head>
    <body>
        <div id="content">
            <div id="search-bar-container">
              <input id="search-bar-input" name="input-text" type="text" placeholder="Enter topic" />
              <button id="search-bar-submit" type="submit" onclick="checkForText()">Build Collage</button>
            </div>
        </div>
        
        <script>
	        document.querySelector("#search-bar-input").addEventListener("keyup", event => {
	          if(event.key !== "Enter") return; // Use `.key` instead.
	          document.querySelector("#search-bar-submit").click(); // Things you want to do.
	          event.preventDefault(); // No need to `return false;`.
	      	});
        </script>
    </body>
</html>
