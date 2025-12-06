chrome.omnibox.onInputChanged.addListener(
	function (text, suggest) {
	suggest([{
				content : " " + text,
				description : "Digite uma categoria para ver no A Casa do Cogumelo"
			}, {
				content : "Mario",
				description : "Mario"
			}, {
				content : "Zelda",
				description : "Zelda"
			}, {
				content : "Pok%C3%A9mon",
				description : "Pok\u00E9mon"
			}, {
				content : "Smash%20Bros",
				description : "Smash Bros"
			},
		]);
});

chrome.omnibox.onInputEntered.addListener(
	function (text) {
	window.open('http://www.acasadocogumelo.com/search/label/' + text);
});

if (localStorage["notiflastposts"] == "true") {
	notifLatestPosts()
}

if (!localStorage.storagepadrao) {
	localStorage["notifposts"] = "true";
	localStorage["notiflastposts"] = "true";
	notifLatestPosts();
	setTimeout(function(){ notifPost() }, 500);
	window.open('paginas/opcoes.html');
	localStorage.storagepadrao = "true";
}

function notifLatestPosts() {
	$.ajax({
		url : 'http://apps.aloogle.net/blogapp/chromenotification.php?site=acasadocogumelo&list=true',
		success : function (json) {
			if (json != localStorage['ultimosposts']) {
				localStorage['ultimosposts'] = json;
				var notificationId = null;
				x = parseInt(localStorage["lastNumber"]) + 1;
				notif = chrome.notifications.create("post" + x, {
						type : "list",
						iconUrl : 'icons/icon_128.png',
						title : '\u00DAltimas not\u00edcias',
						message : '',
						items : [{
								title : '',
								message : JSON.parse(localStorage.getItem("ultimosposts"))[0].titulo
							}, {
								title : '',
								message : JSON.parse(localStorage.getItem("ultimosposts"))[1].titulo
							}, {
								title : '',
								message : JSON.parse(localStorage.getItem("ultimosposts"))[2].titulo
							}, {
								title : '',
								message : JSON.parse(localStorage.getItem("ultimosposts"))[3].titulo
							}, {
								title : '',
								message : JSON.parse(localStorage.getItem("ultimosposts"))[4].titulo
							}
						],
						buttons : [{
								title : 'Abrir o A Casa do Cogumelo',
								iconUrl : 'icons/icon_16.png'
							}
						],
						priority : 2
					}, function (id) {
						notificationId = id;
					});
				chrome.notifications.onClicked.addListener(function (notifId) {
					if (notifId == notificationId) {
						window.open('http://acasadocogumelo.com');
						chrome.notifications.clear(notifId, function (wasCleared) {});
					}
				});
				chrome.notifications.onButtonClicked.addListener(function (notifId, buttonIndex) {
					if (notifId == notificationId) {
						if (buttonIndex == 0) {
							window.open('http://acasadocogumelo.com');
							chrome.notifications.clear(notifId, function (wasCleared) {});
						}
					}
				});
			}
		}
	})
}

function notifPost() {
	$.ajax({
		url : 'http://apps.aloogle.net/blogapp/chromenotification.php?site=acasadocogumelo',
		success : function (json) {
			if (json != localStorage['ultimopost']) {
				localStorage['ultimopost'] = json;
				if (!localStorage["lastNumber"]) {
					localStorage["lastNumber"] = 0;
				}
				var notificationId = null;
				x = parseInt(localStorage["lastNumber"]) + 1;
				notif = chrome.notifications.create("post" + x, {
						type : "basic",
						iconUrl : JSON.parse(localStorage.getItem("ultimopost")).imagem,
						title : JSON.parse(localStorage.getItem("ultimopost")).titulo,
						message : JSON.parse(localStorage.getItem("ultimopost")).descricao,
						buttons : [{
								title : 'Abrir no A Casa do Cogumelo',
								iconUrl : 'icons/icon_16.png'
							}
						],
						priority : 2
					}, function (id) {
						notificationId = id;
					});
				chrome.notifications.onClicked.addListener(function (notifId) {
					if (notifId == notificationId) {
						window.open(JSON.parse(localStorage.getItem("ultimopost")).link);
						chrome.notifications.clear(notifId, function (wasCleared) {});
					}
				});
				chrome.notifications.onButtonClicked.addListener(function (notifId, buttonIndex) {
					if (notifId == notificationId) {
						if (buttonIndex == 0) {
							chrome.tabs.create({
								url : JSON.parse(localStorage.getItem("ultimopost")).link
							});
							chrome.notifications.clear(notifId, function (wasCleared) {});
						}
					}
				});
				localStorage["lastNumber"] = x;
			}
		}
	})
}

setInterval(function () {
	if (localStorage["notifposts"] == "true") {
		notifPost()
	}
}, 60000 * 20);