const { ethers } = require("/usr/lib/node_modules/ethers");

async function myEther() {
    const provider = new ethers.providers.JsonRpcProvider(
        // provider address
        "https://blockchain-solveme-b6f172ca3bb89122-eth.2022.ductf.dev/",
    )
    const signer = new ethers.Wallet(
        // player_wallet private key
        "0xad04e1c46addb505f631c26c26ce8490fe801b4534eaf2cea2111d5cc8e3fb39",
        provider
    )

    // ABI map
    const ABI = [
        {
            "inputs": [],
            "name": "solveChallenge",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "isSolved",
            "outputs": [
                {
                    "internalType": "bool",
                    "name": "",
                    "type": "bool"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        }
    ]

    // contract address of SolveMe.sol
    contract_address = "0x6E4198C61C75D1B4D1cbcd00707aAC7d76867cF8"
    const ChallContact = new ethers.Contract(contract_address, ABI, signer)

    // view blocknum and ballance of player_wallet
    BlockNum = await provider.getBlockNumber()
    Balance = await provider.getBalance(signer.address)
    
    // call solveChallenge()
    await ChallContact.functions.solveChallenge()
    
    // get value of isSolved variable
    data = await ChallContact.functions.isSolved()
    

    console.log(
        {
            "BlockNumber": BlockNum,
            "Balance": eval(Balance["_hex"]),
            "Contact": {
                "data": data,
            }
        }
    )
}

myEther()