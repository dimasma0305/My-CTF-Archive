from flask import Blueprint,request, session
from controllers import main

index_page = Blueprint('main_blueprint', __name__ ,
                        template_folder='templates')

@index_page.route('/',methods=['GET','POST'])
def index():
    Main = main.Main(request)
    if request.method == 'GET':        
        Main.set_view()
    if request.method == 'POST':    
        result_guess = Main.check_guessing(session=session)
        if  result_guess == 'Correct!':
            session[Main.key]['skor'] += 1
            del session[Main.key]['number_guesss'][0]
        if result_guess == 'Wrong!':
            del session[Main.key]   
            Main.set_sign()
        if session[Main.key]['skor'] == 100:
            result_guess = 'Victory!, this is the flag for you: slashroot6{REDACTED}'
        Main.set_view(data={'result_guess' : result_guess})

    return Main()