const { ethers } = require("/usr/lib/node_modules/ethers");

function init_ether(
    provider_addr,
    privkey,
    contract_addr,
    abi,
) {
    if (!abi) abi = [];
    const provider = new ethers.providers.JsonRpcProvider(
        provider_addr,
    )
    const signer = new ethers.Wallet(
        privkey,
        provider
    )
    const ChallContact = new ethers.Contract(contract_addr, abi, signer)
    return ChallContact
}


async function main() {
    // get current block bytecode
    let provider_addr = "http://challs.htsp.ro:9001/88566f54-12ee-4ef5-9113-b921208a9afc"
    let privkey = "0xf89184ec4b9d9871899c81168c40dc48902cd1fe555ac74c42a022fa1134e302"
    let current_contract_addr = "0xc2215C4DE300371Fa08FA7cd73B1168e7C61C75D"
    const current_contract = init_ether(
        provider_addr,
        privkey,
        current_contract_addr
    )
    let current_bytecode = await current_contract.provider.getCode(current_contract_addr)
    console.log("current byte-code")
    console.log(current_bytecode)

    // get vuln bytecode
    let vuln_address = await current_contract.provider.getStorageAt(current_contract.address, 0)
    vuln_address = "0x" + vuln_address.substring(vuln_address.length - 40, vuln_address.length)

    const vuln_contract = init_ether(
        provider_addr,
        privkey,
        vuln_address
    )

    let vuln_bytecode = await vuln_contract.provider.getCode(vuln_address)
    console.log("vuln byte-code")
    console.log(vuln_bytecode) 

}

if (require.main == module) {
    main()
}