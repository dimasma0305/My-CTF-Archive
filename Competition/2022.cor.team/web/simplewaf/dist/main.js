const express = require("express");
const fs = require("fs");

const app = express();

const PORT = process.env.PORT || 3456;

app.use((req, res, next) => {
    if([req.body, req.headers, req.query].some(
        (item) => item && JSON.stringify(item).includes("flag")
    )) {
        // make a log in docker
        fs.appendFileSync("/tmp/log.txt", "False: " + JSON.stringify(req.query) + "\n");
        return res.send("bad hacker!");
    }
    // make a log in docker
    fs.appendFileSync("/tmp/log.txt", "True: " + JSON.stringify(req.query) + "\n");
    next();
});

app.get("/", (req, res) => {
    try {
        res.setHeader("Content-Type", "text/html");
        // make a log in docker
        fs.appendFileSync("/tmp/log.txt", "opening: "+req.query.file + "\n");
        res.send(fs.readFileSync(req.query.file || "index.html").toString());

    }
    catch(err) {
        console.log(err);
        res.status(500).send("Internal server error");
    }
});
app.listen(PORT, () => console.log(`web/simplewaf listening on port ${PORT}`));