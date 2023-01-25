import secrets,os
os.system('rm -rf flask_session')
SESSION_FILE_DIR='flask_session'
SESSION_PERMANENT=True
SECRET_KEY=secrets.token_urlsafe(16)
SESSION_TYPE='filesystem'
