
- files
    - lfi di endpoint files.
        - register/login dulu.
        - http://files.ctf.localhost/?file=../../../../../var/www/html/cleanup.php.

- tasktracer
    - kita bisa mengirimkan message dari current user ke user lain di endpoint tasktracer.
      - curently not tested
    - jika user kita admin maka kita akan mendapatkan flagnya.

- auth
    - jika kita somehow membuat nama kita menjadi admin, maka kita akan bisa mendapatkan flag di endpoint tasktracer.
    - kita tidak bisa register menggunakan nama admin karena regex ini /admin/eg di fungsi register.

- all
    - autentikasi tejadi pada endpoint http://auth.ctf.localhost:8844/auth
        - di auth ini akan di transmit lewat reverse proxy yaitu traefik
        - transmit header remote-user: username

        