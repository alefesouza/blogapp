$(function () {
	$('#notifposts').change(function () {
		localStorage["notifposts"] = document.getElementById("notifposts").checked;
	});
	$('#notiflastposts').change(function () {
		localStorage["notiflastposts"] = document.getElementById("notiflastposts").checked;
	});
	document.getElementById("notifposts").checked = JSON.parse(localStorage["notifposts"]);
	document.getElementById("notiflastposts").checked = JSON.parse(localStorage["notiflastposts"]);
});