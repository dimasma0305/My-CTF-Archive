from web3 import Web3
from web3 import HTTPProvider
from web3.contract import Contract
from pwn import context, log

context.log_level = "INFO"

rpc_url = "http://challs.htsp.ro:9001/16816903-52b7-4573-afeb-f942650e412c"
privkey = "0x913776940f97137b3c5a8b124a2521242b84f7052ed36f9af0ef3193d07b89e9"
current_contract_addr = "0x9C5e027DD9fC18694a05E677b40e97c077EBfF6e"

w3 = Web3(HTTPProvider(rpc_url))


leak = w3.eth.getStorageAt(current_contract_addr, 0).hex()
log.info("leak addr: %s", leak)
vuln_contract_addr = w3.toChecksumAddress("0x"+leak[-40:])
log.info("vuln contract address @ %s", vuln_contract_addr)
log.info("vuln contract bytecode: %s", w3.eth.getCode(vuln_contract_addr).hex())