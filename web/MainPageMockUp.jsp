<%--
  Created by IntelliJ IDEA.
  User: zifanshi
  Date: 2/9/18
  Time: 3:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>MainPage</title>
</head>
<body>
<div id="content"> </div>
<div id = "image"> </div>
<script>
    function hndlr(response) {
        for (var i = 0; i < response.items.length; i++) {
            var item = response.items[i];
            document.getElementById("image").innerHTML += "<img src=" + item.link + ">" + "<br>";
        }
    }
</script>
<!-- Request 3 times to get 30 pictures-->
<script src="https://www.googleapis.com/customsearch/v1?key=AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw
    &cx=003668417098658282383:2ym3vezfm44&q=<%=request.getParameter("topic")%>&start=1&imgSize=medium&searchType=image&filtetype=png&callback=hndlr">
</script>
<script src="https://www.googleapis.com/customsearch/v1?key=AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw
        &cx=003668417098658282383:2ym3vezfm44&q=<%=request.getParameter("topic")%>&start=11&imgSize=medium&searchType=image&filtetype=png&callback=hndlr">
</script>
<script src="https://www.googleapis.com/customsearch/v1?key=AIzaSyDaJ74IGt2X5miRWhriFOImLkBSo1G_dNw
        &cx=003668417098658282383:2ym3vezfm44&q=<%=request.getParameter("topic")%>&start=21&imgSize=medium&searchType=image&filtetype=png&callback=hndlr">
</script>
</body>
</html>