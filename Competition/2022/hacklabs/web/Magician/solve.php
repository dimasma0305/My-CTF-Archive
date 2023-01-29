        
<?php
    session_start();
    include "flag.php";

    $magic_hash = "0e200317437806422913347546193344";
    $sugar = "198";
    $spice = "471";
    $everythingnice = "hello";



    $magic_input = $_POST['magic'];

    for ($i=0; $i < 0xffff; $i++) { 
        $magic_input = $i;
        if(md5($sugar . $magic_input . $spice . $everythingnice) == $magic_hash){
            echo $magic_input."\n";
            break;
        }
        echo $i."\n";
    }

?>

    