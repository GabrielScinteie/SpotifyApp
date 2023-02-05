from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

user_roles_relationship = Table(
    'users_roles', Base.metadata,
    Column('user_id', Integer, ForeignKey('users.id')),
    Column('role_id', Integer, ForeignKey('roles.id')),
    extend_existing=True
)
