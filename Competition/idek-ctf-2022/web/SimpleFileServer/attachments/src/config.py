import random
import os
import time
from datetime import datetime

SECRET_OFFSET = -67198624 # REDACTED
random.seed(round((time.time() + SECRET_OFFSET) * 1000))
os.environ["SECRET_KEY"] = "".join([hex(random.randint(0, 15)) for x in range(32)]).replace("0x", "")
with open("/tmp/secret_key", "w") as f:
    f.write(f"datetime: {datetime.now()}")
    f.write(f"timestamp: {time.time()}\n")
    f.write(f"seed: {round((time.time() + SECRET_OFFSET) * 1000)}\n")
    f.write(f"secret key: {os.environ['SECRET_KEY']}\n")
