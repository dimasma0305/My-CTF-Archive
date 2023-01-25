>not my writeup
>https://discord.com/channels/835621507576496129/879152734396354581

**Chess Challenge:** In app.py we see an 'options' command handler

```python
@socketio.on('options')
def engine_options(options):
    game.get(request.sid).msfrog.configure(options)
```

In enemy.py

```pythonoptions
    def configure(self, options):
        try:
            if "Hash" in options and options["Hash"] > 128:
                return
            if "Threads" in options and options["Threads"] > 1:
                return
            if "Debug Log File" in options:
                return
            self.engine.update_engine_parameters(options)
        except Exception as e:
            print(e)
            self.emit("chat", {"name": "🐸", "msg": "Error configuring engine"})
```

so we can use options to update stockfish parameters from stockfish docs: [https://github.com/official-stockfish/Stockfish](https://github.com/official-stockfish/Stockfish "https://github.com/official-stockfish/Stockfish") we see that there is a skill level (Skill Level) options which we can tamper with making the bot defeatable. (edited)

```http
POST /socket.io/?EIO=4&transport=polling&t=O9xIFlp&sid=2szlbgadDO-i0dmBAFR7 HTTP/2
Host: msfrogofwar.be.ax
Content-Length: 38
Sec-Ch-Ua: "-Not.A/Brand";v="8", "Chromium";v="102"
Accept: */*
Content-Type: text/plain;charset=UTF-8
Sec-Ch-Ua-Mobile: ?0
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36
Sec-Ch-Ua-Platform: "Linux"
Origin: https://msfrogofwar.be.ax
Sec-Fetch-Site: same-origin
Sec-Fetch-Mode: cors
Sec-Fetch-Dest: empty
Referer: https://msfrogofwar.be.ax/
Accept-Encoding: gzip, deflate
Accept-Language: id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7

42["options",{"Threads":1,"Hash":128,"Skill Level":1}]
```

<div id="chat">System: Lost connection to the game server, please restart<br>🐸: i bet your main category is stego<br>🐸: have you tried alt+f4?<br>🐸: ?<br>🐸: so bad lmfaoo<br>🐸: skill issue<br>🐸: mad cuz bad<br>🐸: wtf???<br>🐸: corctf{"ur bad at chess" - Sun Tzu}</div>