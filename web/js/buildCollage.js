function checkForText() {
	let input = document.getElementById("search-bar-input").value;
	if (input.length === 0) {
		// do nothing
		console.log("Input length is 0")
	} else {
		// input exists, start build collage process
		buildCollage();
	}
}

function buildCollage() {
	let keyword = document.getElementById("search-bar-input").value;

	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "ServletEngine?keyword=" + keyword, true);
	xhttp.onload = function (e) {
		if (xhttp.readyState === 4) {
			if (xhttp.status === 200) {
				redirectToSecondPage(xhttp.responseText);
			} else {
				console.error("error: " + xhttp.responseText);
			}
		}
	};
	xhttp.onerror = function (e) {
		console.error(xhttp.statusText);
	};
	xhttp.send();
}

function redirectToSecondPage() {
	let secondPageURL = getURL()
	window.location.replace(secondPageURL);
}

function getURL() {
	var currUrl = window.location.href
	var url = currUrl.substring(0, currUrl.lastIndexOf("/")+1) + "secondpage.jsp";
	return url;
}