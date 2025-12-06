<html>
<head>
  <title>A Casa do Cogumelo</title>
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
		document.getElementById('frame').src = 'http://www.acasadocogumelo.com/search?q=' + document.getElementById('q').value + '&m=1';
	}
	
	function enter() {
		if (event.keyCode == '13') {
			document.getElementById('frame').src = 'http://www.acasadocogumelo.com/search?q=' + document.getElementById('q').value + '&m=1';
		}
	}
	
	function frameClick(nome) {
		document.getElementById('frame').src = 'http://acasadocogumelo.com/search/label/' + nome + '?m=1';
		drawerPanel.togglePanel();
	}
	</script>
</head>

<body unresolved>
	
	 <core-drawer-panel id="drawerPanel">
		 <core-header-panel drawer>
	<img src="icones/drawer_logo.png" width="100%" onclick="document.getElementById('frame').src = 'http://acasadocogumelo.com/?m=1'; drawerPanel.togglePanel();">
	  <core-menu>
	  <? include("partes/drawer.php"); ?>
	  </core-menu>
		 </core-header-panel>

		 <core-header-panel main id="main">
    <core-toolbar id="defaultbar">

    <core-icon-button icon="menu" id="navicon"></core-icon-button>
    <core-icon src="icones/ic_toolbar.png" onclick="document.getElementById('frame').src = 'http://acasadocogumelo.com/?m=1';"></core-icon>
	<div flex></div>
    <core-tooltip label="Buscar" position="bottom" class="fancy">
    <core-icon-button icon="search" onclick="search();" id="search"></core-icon-button>
	</core-tooltip>
    <core-tooltip label="Abrir site" position="bottom" class="fancy">
    <core-icon-button icon="open-in-new" onclick="window.open('http://acasadocogumelo.com/')"></core-icon-button>
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
	<div class="container"><iframe id="frame" style="border: none; width: 400; height: 430;" src="http://www.acasadocogumelo.com/?m=1"></iframe>
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