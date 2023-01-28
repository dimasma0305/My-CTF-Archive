domain
```
snippet.htb
```

name
```
Charlie Rooper
Jean Castux
Thierry Halliday
```

recon
```
ffuf -ic -w /usr/share/wordlists/dirb/common.txt -e '.php' -u "http://snippet.htb/FUZZ" | tee "recon/ffuf_snippet.htb.txt"
nikto -host "http://snippet.htb" | tee "recon/nikto_snippet.htb.txt"
```