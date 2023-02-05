from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()
engine = create_engine('mariadb+mariadbconnector://auth-user:passwduser@192.168.56.10:3306/auth-db')
Session = sessionmaker(bind=engine)
