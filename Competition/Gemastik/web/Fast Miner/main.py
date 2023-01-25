#!/usr/bin/env python3
import os
from functools import wraps
from hashlib import sha256
from typing import Dict, Optional
from uuid import uuid4 as uuid

from bottle import get, post, request, response, run
    
# holds notes content
notes: Dict[str, str] = {
    "00000000": os.environ.get("FLAG", "FLAG{test_flag}"),
}

# holds uid
session: Dict[str, bytes] = {}


@get("/")
def index():
    sessionId = request.cookies.get("sessionId")

    if sessionId is None:
        sessionId = str(uuid())
        value = uuid().bytes
        session[sessionId] = value
        response.set_cookie("sessionId", sessionId)
        print(f"session: {session}")

    value = session.get(sessionId)
    print(f"value: {value}")
    print(f"notes: {notes}")
    if value is None:
        return "unauthorized"

    uid = value.hex()
    return f"hi {uid}"


@get("/notes")
def get_note():
    sessionId = request.cookies.get("sessionId")
    if sessionId is None:
        return "unauthorized"

    uid = session.get(sessionId)
    if uid is None:
        return "unauthorized"

    title: Optional[str] = request.query.get("title")
    if title is None:
        return "not found"

    titleB = title.encode("latin-1", "ignore")
    print(f"titleB: {titleB}")

    hash = sha256(uid[:2])
    hash.update(titleB)
    print(f"{uid[:2]}: {hash.hexdigest()}")

    # get the last 4 bytes as index
    idx = hash.hexdigest()[-8:]
    print(f"idx: {idx}")
    note = notes.get(idx)

    if note is None:
        return "not found"

    return note


@post("/notes")
def create_note():
    sessionId = request.cookies.get("sessionId")
    if sessionId is None:
        return "unauthorized"

    uid = session.get(sessionId)
    if uid is None:
        return "unauthorized"

    title: Optional[str] = request.forms.get("title")
    content: Optional[str] = request.forms.get("content")

    if title is None:
        return "title can't be empty"

    titleB = title.encode("latin-1", "ignore")
    print(f"titleB: {titleB}")
    hash = sha256(uid[:2])
    hash.update(titleB)
    print(f"uid[:2]: {uid[:2]}")
    print(f"post hash: {hash.hexdigest()}")

    # get the last 4 bytes as index
    idx = hash.hexdigest()[-8:]
    print(f"post idx: {idx}")
    
    if notes.get(idx) is not None:
        return "unauthorized"

    notes[idx] = content
    print(f"notes: {notes}")
    return idx


run(server="waitress", host="0.0.0.0", port=8000)
