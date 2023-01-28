import pickle
from base64 import b64decode

class Elf:...
a = pickle.loads(b64decode("gASVUAAAAAAAAACMCF9fbWFpbl9flIwDRWxmlJOUKYGUfZQojARuYW1llIwJU25vd2ZsYWtllIwIYWN0aXZpdHmUjA1QYWNraW5nIGdpZnRzlIwCaWSUTnViLg=="))
print(a.__dict__)