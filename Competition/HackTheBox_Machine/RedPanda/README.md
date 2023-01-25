```sh
cat /opt/panda_search/src/main/java/com/panda_search/htb/panda_search/MainController.java
```

```java
try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/red_panda", "woodenk", "RedPandazRule");
            stmt = conn.prepareStatement("SELECT name, bio, imgloc, author FROM pandas WHERE name LIKE ?");
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                ArrayList<String> panda = new ArrayList<String>();
                panda.add(rs.getString("name"));
                panda.add(rs.getString("bio"));
                panda.add(rs.getString("imgloc"));
                panda.add(rs.getString("author"));
                pandas.add(panda);
            }
        }catch(Exception e){ System.out.println(e);}
```

```
2022/07/12 11:30:01 CMD: UID=1000 PID=5665   | /bin/bash /opt/cleanup.sh 
2022/07/12 11:30:01 CMD: UID=1000 PID=5670   | /usr/bin/find /dev/shm -name *.xml -exec rm -rf {} ; 
2022/07/12 11:30:01 CMD: UID=1000 PID=5675   | /bin/bash /opt/cleanup.sh 
2022/07/12 11:30:01 CMD: UID0x0a790a0a=1000 PID=5681   | /usr/bin/find /tmp -name *.jpg -exec rm -rf {} ; 
2022/07/12 11:30:01 CMD: UID=???  PID=5682   | ???
2022/07/12 11:30:01 CMD: UID=1000 PID=5683   | /usr/bin/find /dev/shm -name *.jpg -exec rm -rf {} ; 
2022/07/12 11:30:01 CMD: UID=1000 PID=5684   | /usr/bin/find /home/woodenk -name *.jpg -exec rm -rf {} ; 
```

```bash
#!/bin/bash
/usr/bin/find /tmp -name "*.xml" -exec rm -rf {} \;
/usr/bin/find /var/tmp -name "*.xml" -exec rm -rf {} \;
/usr/bin/find /dev/shm -name "*.xml" -exec rm -rf {} \;
/usr/bin/find /home/woodenk -name "*.xml" -exec rm -rf {} \;
/usr/bin/find /tmp -name "*.jpg" -exec rm -rf {} \;
/usr/bin/find /var/tmp -name "*.jpg" -exec rm -rf {} \;
/usr/bin/find /dev/shm -name "*.jpg" -exec rm -rf {} \;
/usr/bin/find /home/woodenk -name "*.jpg" -exec rm -rf {} \;
```


# Reference
- https://book.hacktricks.xyz/pentesting-web/ssti-server-side-template-injection#java
- https://github.com/payloadbox/ssti-payloads
- https://www.acunetix.com/blog/web-security-zone/exploiting-ssti-in-thymeleaf/
- https://raw.githubusercontent.com/VikasVarshney/ssti-payload/master/ssti-payload.py
- https://synisl33t.com/2022/07/10/htb-red-panda/
- https://javamana.com/2021/11/20211121071046977B.html