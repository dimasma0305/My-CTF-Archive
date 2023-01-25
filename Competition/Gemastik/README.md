- cek listening port
  - sudo lsof -i -P -n | grep LISTEN
- cek service running 
  - sudo ps aux
- cek setuid file
  - find / -user root -perm -4000 -exec ls -ldb {} \; 2> /dev/null
- change mysql default password
  - mysql_secure_installation
- hardening ssh
  - disable root login
- restrict user lain untuk melihat directory dari user yang ada
- checking for ssrf
- checking for redis
```
	curl -L https://github.com/carlospolop/PEASS-ng/releases/latest/download/linpeas.sh | sh
```
