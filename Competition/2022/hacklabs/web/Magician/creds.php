        
<?php
    session_start();
    include "flag.php";

    $magic_hash = "0e200317437806422913347546193344";
    $sugar = "198";
    $spice = "471";
    $everythingnice = "hello";


    if(isset($_POST['magic'])){

        $magic_input = $_POST['magic'];

        if(md5($sugar . $magic_input . $spice . $everythingnice) == $magic_hash){
            
            $_SESSION['flag'] = $flag;

            ?>
                <script>window.location.href="../worthy.php"</script>
            <?php

        } else {
            die("YOU ARE NOT A MAGICIAN!");
        }  

    }

?>

    