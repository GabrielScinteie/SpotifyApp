# python -m pip install lxml spyne
import jwt
from spyne import Application, rpc, ServiceBase, Integer, Double, String
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
import logging
from wsgiref.simple_server import make_server

from spyne.util.wsgi_wrapper import WsgiMounter

from jws import create_jws, SECRET_KEY, ALGORITHM
from repositories.user_repository import create_user, get_users, change_pass, delete_user, add_role, get_roles, \
    get_user_id_from_username, get_roles_by_id


class IdentityManagementService(ServiceBase):
    @rpc(String, String, _returns=String)
    def create_user(self, username, password):
        return create_user(username, password)

    @rpc(_returns=String)
    def get_users(self):
        return get_users()

    @rpc(String, String, String, _returns=String)
    def change_pass(self, username, password, new_pass):
        return change_pass(username, password, new_pass)

    @rpc(String, String, _returns=String)
    def delete_user(self, username, password):
        return delete_user(username, password)

    @rpc(String, String, _returns=String)
    def add_role(self, username, role_name):
        return add_role(username, role_name)

    @rpc(String, String, _returns=String)
    def login(self, username, password):
        response = "Date invalide"
        serialized_users = get_users()
        lines = serialized_users.split("\n")
        users = []
        for line in lines[:-1]:
            nume, parola = line.split("||")
            users.append((nume, parola))

        for user in users:
            if username == user[0]:
                if password == user[1]:
                    response = create_jws(username, password)
                else:
                    response = "Parola gresita."
        return response

    @rpc(String, _returns=String)
    def logout(self, my_jwt):
        f = open("invalidTokens.txt", "a")
        f.write(my_jwt + "\n")
        return "JWT invalidat!"

    @rpc(String, _returns=String)
    def authorize(self, my_jwt):
        f = open("invalidTokens.txt", "r")
        jwts = f.readlines()
        for blocked_jwt in jwts:
            if my_jwt == blocked_jwt:
                return "Eroare, JWT se afla in blocked list!"

        try:
            jwt.decode(my_jwt, SECRET_KEY, algorithms=ALGORITHM)
        except:
            return "Eroare, JWT semnatura invalida!"

        decoded = jwt.decode(my_jwt, SECRET_KEY, algorithms=ALGORITHM)

        user_id = decoded["sub"]
        roles_jwt = decoded["role"]
        roles_jwt = roles_jwt.split("||")
        roles_jwt = roles_jwt[:-1]

        exista_userID = False
        roles = []

        users_lines = get_users().split("\n")[:-1]
        users_id = []

        for line in users_lines:
            vector = line.split("||")
            name = vector[0]
            users_id.append(int(get_user_id_from_username(name)))

        for user in users_id:
            if int(user_id) == user:
                exista_userID = True
                roles = get_roles_by_id(user)
                roles = roles.split("||")

        roluri_pretinse = len(roles_jwt)
        for role_jwt in roles_jwt:
            for role in roles:
                if role_jwt == role:
                    roluri_pretinse -= 1

        if roluri_pretinse != 0 or exista_userID == False:
            f = open("invalidTokens.txt", "a")
            f.write(my_jwt + "\n")
            f.close()
            if exista_userID == False:
                return "Eroare, invalid user_id!"
            else:
                return "Eroare, rol pretins invalid!"

        response = str(user_id) + "||"
        response = response + ' '.join(str(element) for element in roles_jwt)

        return response


application = Application([IdentityManagementService], 'services.dbManager.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:7999")
    logging.info("wsdl is at: http://127.0.0.1:7999/?wsdl")

    server = make_server('127.0.0.1', 7999, wsgi_application)
    server.serve_forever()


