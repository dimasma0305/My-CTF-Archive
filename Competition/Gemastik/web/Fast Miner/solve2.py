from hashlib import sha256
from typing import Dict, Tuple
import requests

def get_block_data(num=25) -> Dict[bytes, bytes]:
    latest_block = requests.get("https://blockchain.info/q/latesthash").text

    cur_block = latest_block
    res = {}

    for _ in range(num):
        resp = requests.get(f"https://blockchain.info/rawblock/{cur_block}",
                            params={"format": "hex"})
        block_data = resp.text
        header = bytes.fromhex(block_data[:160])

        h = sha256(header)
        hash = h.digest()

		# store data
        res[hash[:2]] = hash[2:]

        # reverse the byte order
        prev_block = header[35:3:-1].hex()

        # change cur_block to prev_block for the next iteration
        cur_block = prev_block

    return res

def get_uid() -> Tuple[str, bytes]:
    resp = requests.get("http://localhost:8000")
    uid = bytes.fromhex(resp.text.split()[-1][:4])

    return resp.cookies.get("sessionId"), uid

def main():
    block = get_block_data(12)
    while True:
        sessionId, uid = get_uid()
        title = block.get(uid)
        if title is not None:
            break

    assert sha256(uid + title).hexdigest().endswith("0000000000000000")

    resp = requests.get("http://localhost:8000/notes",
                        cookies={"sessionId": sessionId},
                        params={"title": title})
    print(resp.text) # flag

if __name__ == "__main__":
    main()