<?php

$cwd = getcwd();
file_put_contents("file://$cwd/testing.txt", "asdasd");
echo getcwd();

?>