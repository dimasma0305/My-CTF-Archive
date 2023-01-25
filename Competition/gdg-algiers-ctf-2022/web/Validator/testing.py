from schema import Schema, And, SchemaError

class MyDict(dict):
    def __getattr__(self, *args, **kwargs):
        return self.get(*args, **kwargs)
    def __setattr__(self, *args, **kwargs):
        return self.__setitem__(*args, **kwargs)

def wrap_error(e: Exception):
    return f"{e.__class__.__name__}: {e}"

res = MyDict()

sdict = {"dimas":"str"}
data = {"dimas": "testing"}
s = Schema(sdict, error="{.__setattr__.__globals__}")

try:
    s.validate(MyDict(data))
    res.isError = False
except SchemaError as e:
    res.message = wrap_error(e)
    res.isError = True

print(res)