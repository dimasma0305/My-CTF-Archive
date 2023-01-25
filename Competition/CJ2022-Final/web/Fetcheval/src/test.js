const url = require('url')

let webUrl = `http://localhost.ap-1.sharedwithexpose.com`
const hostname = url.parse(webUrl).hostname;

console.log(hostname)