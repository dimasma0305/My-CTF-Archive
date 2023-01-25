<!DOCTYPE html>
<html style="height:100%;width:100%;"><script>

</script><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Cisco WebVPN Service</title>
<meta name="HandheldFriendly" content="true">
<meta name="viewport" content="width=500,user-scalable=no">
<link href="./template_files/portal.css" rel="stylesheet" type="text/css">
<link href="./template_files/logon_custom.css" rel="stylesheet" type="text/css">
<style>
     .auth-page-title {color:#800000;font-size:150%;font-weight:bold;background-image:url('/+CSCOU+/gradient.gif?r=255&g=255&b=255');}
.auth-page-form-title {background-color:#666666;color:#ffffff;}

</style>

</head><div id="abineFillElement"></div>

<body style="background-color:#ffffff; overflow:auto;height:100%; width:100%; " onload="scrollToLogonForm();blinkDapMessage();">
<table style="width:100%; height: 100%" cellspacing="0" cellpadding="0">
<tbody><tr><td valign="top">
<table style="width:100%;" cellspacing="0" cellpadding="0">

<tbody><tr>
<td style="border-bottom:1px solid #aaaaaa;vertical-align:top; " colspan="2">

<table style="width:100%" border="0" cellpadding="0" cellspacing="0" class="cuesHeaderBg">
<tbody><tr width="100%">
<td class="cuesHeaderAccent" width="100%" colspan="2"></td>

</tr>
   <tr id="title" width="100%">
      <td colspan="2" class="auth-page-title" style="height:40px; padding: 8px" width="100%"><img src="./template_files/csco_logo.gif" align="absmiddle" alt="Logo" title="Logo">&nbsp;&nbsp;WebVPN Service</td>
<!-- <td class="auth-page-title" style="heigh:40px; padding: 8px" align="right">


</td> -->
</tr>
</tbody></table>
</td>
</tr>

<tr style="height:40px">
<td align="right" colspan="2">

</td>
</tr>
<tr align="center">

<td id="form_panel" align="middle">
<div style="margin-top:100px;padding:3em;">
<form id="unicorn_form" method="POST" autocomplete="off" action="/" abineguid="3C61B3092C6941168A3709610AB5C794">
<input name="tgroup" type="hidden" value="">
<input name="next" type="hidden" value="">
<input name="tgcookieset" type="hidden" value="">

<table style="width:350px; border:0px !important;padding:0px;" id="form_table" cellspacing="0" cellpadding="0" border="0">

<tbody><tr border="0">
    <td colspan="2" id="logon" align="middle" valign="top" border="0" style="padding:0px">
    <!-- <table style="color:#ffffff; ;background-color:#666666;; border-bottom: 1px solid #858A91; width:100%" id="form_title" cellspacing=0  border="0"> -->
	<table style="background-color:rgba(0,0,0,0);padding:0px;border:0px;" id="form_title" cellspacing="0" cellpadding="0" border="0">
	    <tbody><tr height="40">
	   	<td id="form_title_text" align="left" nowrap="" style="padding: 0px; border:0px;background-color:rgba(0,0,0,0);" width="51"><img src="./template_files/login-header-icon.jpg" width="51" style="display:block;"></td>
		<td width="100%" nowrap="" style="background-color: #ffffff; border:0px;" border="0">
			<table style="width:100%" cellspacing="0" cellpadding="0" border="0">
				<tbody><tr>
					<td colspan="2" height="16" style="background-color: #ffffff;border:0px;"></td>
				</tr>
				<tr>
					<td width="100%" align="left" nowrap="" style="background: url(&#39;./template_files/login-header-middle.jpg&#39;); color: #7d8184;font-weight: bold;border:0px;padding-right:30px;text-align:center;">Login</td>
					<td width="1" style="border:0px;"><img src="./template_files/login-header-end.jpg" width="1" style="display:block;"></td>
				</tr>
			</tbody></table>
		</td>
    	</tr>
    	</tbody></table>
    </td>
</tr>

<tr align="center">
<td colspan="2" align="middle" style="color:#000000; background-color:#ffffff;border-left:1px solid #858A91;border-right:1px solid #858A91;"><div id="logon_message_text" style="margin-right:15px;margin-left:15px;margin-top:15px; margin-bottom:5px">Please enter your username and password.</div></td>  </tr> 
<tr align="center"><td colspan="2" align="middle" style="color:#000000; background-color:#ffffff;border:1px solid #858A91;border-top: 0px;">
<div style="margin-left:20;margin-right:20;">
<table border="0" style="color:#000000; background-color:#ffffff;; text-align:left;">
<tbody><tr><td colspan="2" style="margin-left:40">&nbsp;</td></tr>

<tr>
    <td>
    GROUP:
    </td>
    <td>
    <div><select id="group_list" name="group_list" style="z-index:1; float:left;" >
    <option value="DefaultWEBVPNGroup" selected="">Childrens_Medical_Users</option>

    <option value="BasicPortal-WebVPN">RemoteAccess-BasicPortal</option>

    <option value="TestPhoneFactor">Z_ISTestPF</option>

    </select>
    </div>
    </td>
</tr>

    <tr height="20">
    <td nowrap="" id="username_field">
    USERNAME:
    </td>
    <td>
    <input autocomplete="off" id="username" name="username" style="border:1px solid gray; " value="">
    </td>
    </tr>

    <tr>
    <td nowrap="" id="password_field">
    PASSWORD:
    </td>
    <td>
    <input autocomplete="off" id="password_input" name="password" style="border:1px solid gray;" type="password">
    </td>
    </tr>

<tr height="40" align="center">
<td colspan="2" align="middle">
<input style="font-weight: bold; cursor: pointer;" type="submit" name="Login" value="Login">
</td>
</tr>

</tbody></table>
</div></td></tr></tbody></table>
</form></div>

<script>

function set_focus() {
     var name="username";
     if (!name) {
          <!-- If there are no form fields, select the login button -->
          name = "Login";
     }
     if (name) {
          var obj = document.getElementById(name);
          if(obj){
               obj.focus();
          }
     }
}
set_focus();

</script>
</td>

</tr>
</tbody></table>
</td></tr>



</tbody></table>


</div>

</body><div class="abineContentPanel" style="background-image: initial; background-position: initial; background-size: initial; background-repeat: initial; background-attachment: initial; background-origin: initial; background-clip: initial; background-color: transparent !important; margin: 0px !important; padding: 0px !important; opacity: 1 !important; z-index: 2147483647 !important; position: absolute !important; overflow: hidden !important; border-width: 0px !important; visibility: visible !important; display: none;"><iframe class="abineContentFrame" width="450px" allowtransparency="true" frameborder="0" height="0px" scrolling="no" src="./template_files/panel.html" id="abine11794087doNotRemove" style="position:relative !important;display:block !important;background:transparent !important;border-width:0px !important;left:0px !important;top:0px !important;visibility:visible !important;opacity:1 !important;filter:alpha(opacity:100) !important;margin:0 !important;padding:0 !important;height:0px !important;width:450px"></iframe></div></html>
