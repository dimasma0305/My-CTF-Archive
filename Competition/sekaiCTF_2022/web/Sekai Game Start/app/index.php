<?php
include('./flag.php');
class Sekai_Game{
    public $start = True;
    public function __destruct(){
        if($this->start === True){
            echo "Sekai Game Start Here is your flag ".getenv('FLAG');
        }
    }
    public function __wakeup(){
        $this->start=False;
    }
}
print_r($_GET);
if(isset($_GET['    '])){
    unserialize($_GET['sekai_game.run']);
}else{
    highlight_file(__FILE__);
}

?>