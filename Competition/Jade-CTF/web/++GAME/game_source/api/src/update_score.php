<?php
    error_reporting(0);
    include "/var/www/db/secret.php";
    $database=array();
    $database=unserialize(file_get_contents('/var/www/db/database.bin'));
    if(isset($_GET["username"]) && isset($_GET["next_level"]) && isset($_GET["signature"])){
        $username=$_GET["username"];
        $next_level=$_GET["next_level"];
        $signature=$_GET["signature"];

        $concatenated=$username.$next_level.$secret;
        $computed=sha1($concatenated);
        // debug
        echo "concatenated  : ".$concatenated."\n";
        echo "computed      : ".$computed."\n";
        echo "signature     : ".$signature."\n";

        if($signature===$computed){
            echo "success";
            $database[$username]['score']=$next_level;
            file_put_contents('/var/www/db/database.bin', serialize($database));
            http_response_code(200);
        }
        else{
            http_response_code(404);
        }
    }
    else{
        http_response_code(404);
    }
?>