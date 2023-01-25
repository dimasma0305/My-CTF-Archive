import requests

URL = "http://kit-cms1.kitctf.me:9100/web.archive.org/web/20180312161642/http:/www.kit.edu/search.php"

def search_query(query, url=URL):
    res = requests.get(f"{url}?searchquery={query}&scope=kit&search=suchen")
    return res.text

print(search_query("asdsad"))