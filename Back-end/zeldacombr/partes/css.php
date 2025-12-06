<style shim-shadowdom>
html,body {
	height: 100%;
	margin: 0;
	background-color: #E5E5E5;
	font-family: 'RobotoDraft', sans-serif;
}

core-header-panel {
	height: 100%;
	overflow: auto;
	-webkit-overflow-scrolling: touch;
}

core-toolbar {
	background: #2e7d32;
	color: white;
}
	
[drawer] {
	background-color: #ffffff;
}
	
core-drawer-panel:not([narrow]) #navicon {
	display: none;
}

paper-shadow.clickable:active {
	background: #dddddd;
}
	
a {
	text-decoration: none;
}
	
a:hover {
	text-decoration: underline;
}
	
.littlecard {
	position:relative;
	text-align: center;
	font-weight: bold;
	height: auto;
	left: 0;
	right: 0;
}
	
.topbutton {
	position: fixed;
	right: 3%;
	bottom: 3.5%;
	z-index: 2;
}
	
#loadalfabetica {
	position: absolute;
	bottom: 40%;
	margin-top: -2px;
	width: 100%;
	display: none;
}
	
@media (min-width: 600px) {
	#tabs {
		width: 100%;
	}
	
	.littlecard {
		position:relative;
		text-align: center;
		font-weight: bold;
		height: auto;
		width: 90%;
	}
	
	#loadalfabetica {
		bottom: 50%;
	}
	
	core-tooltip.fancy::shadow .core-tooltip {
		opacity: 0;
		-webkit-transition: all 300ms cubic-bezier(0,1.92,.99,1.07);
		transition: all 300ms cubic-bezier(0,1.92,.99,1.07);
		-webkit-transform: translate3d(0, -10px, 0);
		transform: translate3d(0, -10px, 0);
	}

	core-tooltip.fancy:hover::shadow .core-tooltip,
	core-tooltip.fancy:focus::shadow .core-tooltip {
		opacity: 1;
		-webkit-transform: translate3d(0, 0, 0);
		transform: translate3d(0, 0, 0);
	}
}
	
.custom /deep/ ::-webkit-input-placeholder {
	color: grey;
}
	
.custom /deep/ ::-moz-placeholder {
	color: grey;
}
	
.custom /deep/ :-ms-input-placeholder {
	color: grey;
}

.custom /deep/ .label-text,
.custom /deep/ .error {
	color: grey;
}

.custom /deep/ .unfocused-underline {
	background-color: #ffffff;
}
	
.custom[focused] /deep/ .floated-label .label-text {
	color: #ffffff;
}

.custom /deep/ .focused-underline {
	background-color: #ffffff;
}

.custom /deep/ .cursor {
	background-color: #ffffff;
}

.custom {
	color: #ffffff;
}

core-menu {
	width: 100%;
}

core-item.core-selected {
 font-weight: none;
}
	
core-item:hover {
	background: #eeeeee;
}
</style>