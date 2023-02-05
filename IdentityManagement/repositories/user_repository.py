from models.user_orm import User
from base.sql_base import Session
from models.role_orm import Role
from models.users_roles_orm import user_roles_relationship


def get_users():
    session = Session()
    users = session.query(User).all()
    serialized_users = ""
    for user in users:
        serialized_users += user.username + "||" + user.password + "\n"
    # for user in users:
    #     serialized_users += user.username + ": "
    #     for role in user.roles:
    #         serialized_users += role.name + " "
    #     serialized_users += '\n'

    return serialized_users


def create_user(username, password):
    session = Session()
    user = User(username, password)
    try:
        session.add(user)
        session.commit()
        return f"User added succesfully - {user}"
    except Exception as exc:
        return f"Failed to add user - {exc}"


def change_pass(username, password, new_pass):
    session = Session()
    try:
        session.query(User).filter(
            User.username == username and
            User.password == password
        ).update({'password': new_pass})
        session.commit()
        return "Succesful password changed"
    except Exception as exc:
        return f"Failed password change - {exc}"


def delete_user(username, password):
    session = Session()
    user_id = session.query(User).filter(User.username == username)[0].id
    try:
        print(user_id)
        stmt = "DELETE FROM users_roles WHERE user_id = " + str(user_id) + ";"
        session.execute(stmt)
        session.commit()
        session.query().filter(User.username == username and User.password == password).delete()
        session.commit()
        return "Successful user delete"
    except Exception as exc:
        return f"Failed user delete - {exc}"


def add_role(username, role_name):
    session = Session()
    user_id = session.query(User).filter(User.username == username).first().id
    role_id = session.query(Role).filter(Role.name == role_name).first().id
    try:
        stmt = user_roles_relationship.insert().values(user_id=user_id, role_id=role_id)
        session.execute(stmt)
        session.commit()
        return "Role inserted"
    except Exception as exc:
        return f"Failed to insert role - {exc}"


def get_user(username: str):
    session = Session()
    user = session.query(User).filter(User.username == username)[0]
    user_id = ""
    if user:
        user_id = user.id

    return user_id


def get_roles(username):
    session = Session()
    user = session.query(User).filter(User.username == username)[0]
    serialized_roles = ""
    if user:
        for role in user.roles:
            serialized_roles += role.name + "||"

    return serialized_roles

def get_roles_by_id(id):
    session = Session()
    user = session.query(User).filter(User.id == id)[0]
    serialized_roles = ""
    if user:
        for role in user.roles:
            serialized_roles += role.name + "||"

    return serialized_roles


def get_user_id_from_username(username):
    session = Session()
    user = session.query(User).filter(User.username == username)[0]
    serialized_user = ""
    if user:
        serialized_user += str(user.id)
    return serialized_user
