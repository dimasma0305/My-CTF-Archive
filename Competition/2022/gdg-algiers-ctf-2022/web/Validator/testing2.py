class MyDict(dict):
    def __getattr__(self, *args, **kwargs):
        return self.get(*args, **kwargs)
    def __setattr__(self, *args, **kwargs):
        return self.__setitem__(*args, **kwargs)

print(MyDict().__setattr__.__globals__.app)