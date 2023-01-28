const { ethers } = require("/usr/lib/node_modules/ethers");

async function get(){
    provider = new ethers.providers.JsonRpcProvider(
        "https://blockchain-solveme-b6f172ca3bb89122-eth.2022.ductf.dev/",
    )
    signer = await provider.getSigner("0x945FFa2392E952AC330AE62a4aa22Af1f1A6F0AB") // no arg so using accounts[0]
    
    tx = {to: "0x6E4198C61C75D1B4D1cbcd00707aAC7d76867cF8", data: '0x1234' } 
    console.log(await signer.sendTransaction(tx)) 
}
get()