<html>
<head>
  <title>Zelda.com.br</title>
  <? include("partes/head.php") ?>
  <? include("partes/polymer.imports.php"); ?>
  <? include('partes/css.php'); ?>
<style>
html, body{
	overflow: hidden;
}

.container {
	overflow: hidden;
}
</style>
<script>
	function search() {
		document.getElementById('searchbar').style.display = '';
		document.getElementById('defaultbar').style.display = 'none';
	}
		
	function cancel() {
		document.getElementById('defaultbar').style.display = '';
		document.getElementById('searchbar').style.display = 'none';
	}
		
	function doSearch() {
		document.getElementById('frame').src = 'http://www.zelda.com.br/search/node/' + document.getElementById('q').value;
	}
	
	function enter() {
		if (event.keyCode == '13') {
			document.getElementById('frame').src = 'http://www.zelda.com.br/search/node/' + document.getElementById('q').value;
		}
	}
	
	function frameClick(nome) {
		document.getElementById('frame').src = 'http://www.zelda.com.br/' + nome + '?m=1';
		drawerPanel.togglePanel();
	}
	
	function canAccessIFrame(iframe) {
    var html = null;
    try { 
      // deal with older browsers
      var doc = iframe.contentDocument || iframe.contentWindow.document;
      html = doc.body.innerHTML;
    } catch(err){
      // do nothing
    }

    return(html !== null);
}

	window.onload = function() {
		setInterval(function() {
		if(canAccessIFrame(document.getElementById("frame")) == true) {
			document.querySelector('#topbutton').style.display = 'none'
		} else {
			document.querySelector('#topbutton').style.display = '';
		} }, 1000);
	}
	</script>
</head>

<body unresolved>
	
	<core-tooltip label="Voltar ao início" position="top" class="fancy topbutton">
	<paper-fab mini icon="home" id="topbutton" style="display: none; background: #ffc108;" onclick="document.getElementById('frame').src = 'alooglelayout.php';"></paper-fab>
	</core-tooltip>
	
	 <core-drawer-panel id="drawerPanel">
		 <core-header-panel drawer>
	<img src="icones/drawer_logo.png" width="100%" onclick="document.getElementById('frame').src = 'http://www.zelda.com.br'; drawerPanel.togglePanel();">
	  <core-menu>
	  <? include("partes/drawer.php"); ?>
	  </core-menu>
		 </core-header-panel>

		 <core-header-panel main id="main">
    <core-toolbar id="defaultbar">

    <core-icon-button icon="menu" id="navicon"></core-icon-button>
    <core-icon src="icones/ic_toolbar.png" onclick="document.getElementById('frame').src = 'http://www.zelda.com.br';"></core-icon>
	<span flex>Zelda.com.br</span>
    <core-tooltip label="Buscar" position="bottom" class="fancy">
    <core-icon-button icon="search" onclick="search();" id="search"></core-icon-button>
	</core-tooltip>
    <core-tooltip label="Abrir site" position="bottom" class="fancy">
    <core-icon-button icon="open-in-new" onclick="window.open('http://www.zelda.com.br')"></core-icon-button>
	</core-tooltip>
    </core-toolbar>
			 
	<core-toolbar style="display: none;" id="searchbar">
	<paper-input label="Buscar" flex class="custom" id="q" name="q" value="<? echo $busca; ?>" onkeypress="enter()"></paper-input>
   <core-tooltip label="Fazer busca" position="bottom" class="fancy">
	   <core-icon-button icon="search" id="searchform" onclick="doSearch();"></core-icon-button>
	</core-tooltip>
	<core-tooltip label="Cancelar" position="bottom" class="fancy">
		<core-icon-button icon="close" id="cancel" onclick="cancel();"></core-icon-button>
	</core-tooltip>
    </core-toolbar>
	<div class="container"><iframe id="frame" style="border: none; width: 400; height: 430;" src="alooglelayout.php"></iframe>
	</div>
	</core-header-panel>

	  </core-drawer-panel>
</body>
	<script>
document.addEventListener('polymer-ready', function() {
  var navicon = document.getElementById('navicon');
  var drawerPanel = document.getElementById('drawerPanel');
  navicon.addEventListener('click', function() {
    drawerPanel.togglePanel();
  });
});
	</script>

</html>