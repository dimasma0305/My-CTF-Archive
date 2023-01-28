<?php

$page = "home";
if (isset($_GET['page'])) {
    $page = $_GET['page'];
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Web Kegabutan</title>
</head>
<body>
        <h1>Web Kegabutan</h1>
        <a href="/index.php?page=home">Home</a> | <a href="/index.php?page=member&name=">Daftar Member</a>
        <hr>
        <?php include $page . ".php"; ?>
</body>
</html>
