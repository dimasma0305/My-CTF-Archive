import os, struct, base64, random
from flask import render_template, make_response, session

class Rand():

    def set_sign(self):
        key = self.check_key()
        if key:
            self.sign = self.request.cookies.get('_sign')
            self.key = key

        else:
            self.key = int.from_bytes(os.urandom(7), 'big')
            self.sign = base64.b64encode(struct.pack('d',(self.key >> 3) * 2**-53))
            self.set_seed()
            session[self.key] = {'skor' : 0, 'number_guesss': [self.random() for i in range(100)]}

    def check_key(self) -> bool:
        try:
            bytes_urandom = base64.b64decode(self.request.cookies.get('_sign'))
            key = int.from_bytes(bytes_urandom,'big')
            if session.get(key) != None:
                return key
            return 0

        except Exception as e:
            print('ERROR',e)            
            return 0

    def set_seed(self) ->  bytes:
        random.seed(self.key)
        random.seed(struct.pack('d',random.random()))

    def random(self) -> int:
        return random.randint(1,1024)
        
class Main(Rand):
        
    def __init__(self, request, template = 'index.html', data={}) -> None:

        self.template = template
        self.data = data
        self.request = request

        self.set_sign()        

    def __call__(self):
        self.data['skor'] = session[self.key]['skor']
        res = make_response(render_template(self.template,data=self.data))
        res.set_cookie('_sign', self.sign, httponly=True)
        return res

    def check_guessing(self,session):
        number_guess = int(self.request.form.get('number_guess'))
        if session[self.key]['skor'] == 100:
            return 'Victory!, this is the flag for you: slashroot6{REDACTED}'
        if number_guess == session[self.key]['number_guesss'][0]:
            return 'Correct!'
        return 'Wrong!'
        

    def set_view(self,**args) -> make_response:            
        if args.get('data') != None: 
            self.data = args['data']
        if args.get('template') != None: 
            self.template = args['template']

    