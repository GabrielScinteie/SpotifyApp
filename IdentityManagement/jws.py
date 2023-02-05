import uuid
from datetime import datetime, timezone, timedelta

import jwt
from pydantic import BaseModel

from base.sql_base import Session
from models.role_orm import Role
from models.user_orm import User
from repositories.user_repository import get_user, get_roles

SECRET_KEY = "53c8139302bf368217b2cd0d10b176caeeaa08a5ded846a6cf7f34d4fb4aa05c"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 60 * 24 * 10  # 1 zi


def create_jws(username, password):
    user_id = get_user(username)
    roles = get_roles(username)
    print(roles)
    id = uuid.uuid1()
    encoded = jwt.encode({"iss": "http://127.0.0.1:8000",
                          "sub": str(user_id),
                          "pass": hash(password),
                          "role": roles,
                          "exp": datetime.now(tz=timezone.utc) + timedelta(seconds=ACCESS_TOKEN_EXPIRE_SECONDS),
                          "jti": str(id)}, SECRET_KEY, algorithm=ALGORITHM, headers={"alg": ALGORITHM})
    return encoded




