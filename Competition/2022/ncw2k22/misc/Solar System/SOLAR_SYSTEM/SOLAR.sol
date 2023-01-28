// SPDX-License-Identifier: MIT
pragma solidity ^0.8.13;

//@dev: Sanada#7802
contract International_Space_Station{

    // Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune and then the possible Planet Nine
    /**
        Rules:
        1. Challenge ini hanya Introduction to Blockchain Challenge, namun anda DIWAJIBKAN untuk deploy & run .sol ini.
        2. Jika ada pertanyaan terkait soal, silahkan langsung dm Sanada#7802.
        3. Tidak ada limitasi penggunaan software atau website apapun dalam pengerjaan soal.
        4. Flag dibagi menjadi 4 bagian sesuai dengan Jumlah Contract.
        5. dan Semua Rule National Cyber Week 2022 (CTF)
        6. Contract ke-4 Tidak relevan dengan SOLAR.sol, jadi bisa di Comment dulu.

        Format Flag:
        NCW22{1_2_3_4
        1 -> Free Flag ( Decode dari hex, remove "0x")
        2 -> Flag value (hasil return) dari Function "Flagzonia"
        3 -> Function ter-kantong-Friendly
        4 -> **System Protocol have changed!**

        NOTE: - Flag 4 sudah terdapat "}" jadi tidak perlu double }.
    
    @dev : Sanada#7802 
    **/

    bytes32 private flag;
    string private message;
    mapping (address => bool) agreed_rules;
    constructor(){
        flag = 0x7730775f696d5f676f696e675f61726f756e645f7468655f67616c617879;
        message = "Decode from hex, should give you the first part of the Flag for Free!";
    }
    
    function Agree_with_rules() external{
        agreed_rules[msg.sender] = true;
    }

    function free_Flag() external returns (bytes32,string memory){
        require(agreed_rules[msg.sender] == true, "You are required to agree with the rule!");
        return (flag,message);
    }

}

contract Milky_Way{

    uint256 private start_gas;
    uint256 private gas;
    uint256 private gasFee;
    uint256 private usedGas;
    uint256 private next_stop;
    uint256 private maxStop;
    uint256 private answer_1;
    uint256 private answer_2;
    uint256 private answer_3;
    string private message;
    string private message1;
    string private message2;
    string private message3;
    mapping (address => bool) agreed_rules;
    mapping (address => bool) check_1;
    mapping (address => bool) check_2;
    mapping (address => bool) check_3;
    mapping (address => bool) visited_Mercury;
    mapping (address => bool) visited_Venus;
    mapping (address => bool) visited_Earth;
    mapping (address => bool) visited_Mars;
    mapping (address => bool) visited_Jupiter;
    mapping (address => bool) visited_Saturn;
    mapping (address => bool) visited_Uranus;
    mapping (address => bool) visited_Neptune;
    mapping (address => bool) visited_Flagzonia;
    mapping (address => bool) Intergalactic_Visa;

    

    constructor(){
        start_gas = 50;
        gas = 50;
        usedGas = 0;
        maxStop = 5;
        next_stop = 2;
        answer_1 = start_gas + 84539;
        message = "You manage to get here, Congratz.";
        message1= "There you go the first answer is correct!";
    }
    
    function Mercury() external {
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(gas >= 5);
        gasFee = 5;
        gas -= gasFee;
        if(gas != 0 ){
            visited_Mercury[msg.sender] = true;
            usedGas += gasFee;
            next_stop ++;
        }
    }
    
    function Venus() external {
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Jupiter[msg.sender]==true, "Follow the Guiding Light !");
        require(gas - gasFee != 0);
        gasFee = 20;
        gas -= gasFee;
        if(gas != 0){
            visited_Venus[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Earth() external {
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Neptune[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 133;
        gas -= gasFee;
        answer_2 = gasFee;
        if(gas == 0){
            visited_Earth[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Mars() external {
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Saturn[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 8;
        gas -= gasFee;
        if(gas != 0){
            visited_Mars[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Jupiter() external{
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Uranus[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 3;
        gas -= gasFee;
        if(gas != 0){
            visited_Jupiter[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Saturn() external{
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Venus[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 23;
        gas -= gasFee;
        if(gas != 0){
            visited_Saturn[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Uranus() external{
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Mercury[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 15;
        gas -= gasFee;
        if(gas != 0){
            visited_Uranus[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }
    
    function Neptune() external{
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!" );
        require(visited_Mars[msg.sender] == true, "Follow the Guiding Light!");
        require(gas - gasFee != 0);
        gasFee = 29;
        gas -= gasFee;
        if(gas != 0){
            visited_Neptune[msg.sender] = true;
            usedGas += gasFee;
            next_stop++;
        }
    }

    function Visum() external{
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!");
        require(visited_Earth[msg.sender] == true, "You haven't visited your home planet?");
        require(gas != 0, "How could you get a Visa without gas?");
        gasFee = 50;
        gas -= gasFee;
        if(gas == 0){
            Intergalactic_Visa[msg.sender] = true;
            usedGas += gasFee;
        }
    }

    function Agree_with_rules() external{
        agreed_rules[msg.sender] = true;
    }

    function getMaxStop() public view returns(uint256){
        require(agreed_rules[msg.sender] == true, "You are required to agree with the rule!");
        return maxStop;
    }

    function getNextStop() public view returns(uint256){
        require(agreed_rules[msg.sender] == true, "You are required to agree with the rule!");
        return next_stop;
    }

    function getMyGas() public view returns(uint256){
        require(agreed_rules[msg.sender] == true, "You are required to agree with the rule!");
        return gas;
    }

    function gasUsed() external view returns(uint256){
        require(maxStop != 0, "You barely use any!");
        return usedGas;
    }

    function add_Gas(uint256 addedGas) external{
        require(agreed_rules[msg.sender] == true, "You are required to agree with the rule!");
        require(maxStop != 0, "You can no longer suply you ");
        require(gas != 0, "Beep boop, running out of gas~");
        require(maxStop != 0, "You are too far from the gas station!");
        require(next_stop == 2,"You can't land here yet!");
        if(addedGas <= 50){
            gas += addedGas;
            maxStop--;
            next_stop = 0;
            answer_3 = addedGas;
        }
    }

    function Exclusive_Gas(uint256 addedGas) external {
        require(visited_Earth[msg.sender] == true, "This is Exclusive Gas Station! Only Human may Enter!");
        require(gas == 0, "Unless you run out of gas, we'll reject you!");
        require(next_stop == 2);
        if(addedGas == 50){
            gas += addedGas;
        }
    }

    function answer1() external returns(uint256){
        require(visited_Earth[msg.sender] == true, "You are required toa gree with the rule!");
        return answer_1;
    }
    
    function answer2() external returns(uint256){
        require(visited_Earth[msg.sender] == true, "You are required toa gree with the rule!");
        return answer_2;
    }
    
    function answer3() external returns(uint256){
        require(visited_Earth[msg.sender] == true, "You are required toa gree with the rule!");
        return answer_3;
    }

    function Flagzonia() public view returns (uint256){
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!");
        require(visited_Earth[msg.sender] == true, "You haven't visited your homeland?");
        require(Intergalactic_Visa[msg.sender] == true, "You can't land here without Intergalactic Visa!");
        require(gas == 0, "You have some spare gas? What a waste!!");
        uint256 flag = 0x81273829301826362844392 + answer_1 + answer_2 +answer_3;
        return flag;
    }
    
    function Flagzonia_2() public view returns (string memory){
        require(agreed_rules[msg.sender] == true,"You are required to agree with the rule!");
        require(visited_Earth[msg.sender] == true, "You haven't visited your home planet?");
        require(Intergalactic_Visa[msg.sender] == true, "You can't land here without Intergalactic Visa!");
        require(gas == 0, "You have some spare gas? What a waste!!");
        return message;
    }

}

contract Bank_Of_Flagzonia{
    
    string private name;
    string private message;
    string private thanks;
    string private Owner;
    string private Player;
    string private warning;
    string private warning2;
    uint256 private max_deposit;
    uint256 private Owner_Balance;
    uint256 private Player_Balance;
    mapping (address => bool) agreed_rules;
    mapping (address => bool) created_account;
    mapping (address => bool) we_have_a_new_Investor;
    mapping (address => bool) secret_number;
    
    constructor() public{
        Owner = "Monti";
        Owner_Balance = 1000 * (1 ether);
        Player_Balance = 0;
        max_deposit = 1;
        message = "Welcome to Bank of Flagzonia";
        thanks = "Thank You for your Deposit!";
        warning = "You need to agree with the rule!";
        warning2 = "You need to create an Account. Please create One!";
    }

    error Unauthorized(string warning);
    function Agree_with_rules() external returns (string memory){
        agreed_rules[msg.sender] = true;
        return message;
    }

    function Create_Account(string memory _name) public{
        if(agreed_rules[msg.sender] != true)
            revert Unauthorized(warning);
        name = _name;
        created_account[msg.sender] = true;
    }

    function deposit(uint256 amount) public payable returns(string memory){
        if(agreed_rules[msg.sender] != true)
            revert Unauthorized(warning);
        require(created_account[msg.sender] == true, "You don't have an account. Please create One!");
        require(amount < 2000 ether);
        require(max_deposit != 0, "Come again next time!");
        Player_Balance += amount;
        if(amount >= 1000 && amount <= 2000){
            if(amount >= 1100 && amount <= 1900){
                if(amount >=1200 && amount <= 1800){
                    if(amount >= 1300 && amount <= 1700){
                        if(amount >= 1400 && amount <= 1600){
                            if(amount >= 1410 && amount <= 1500){
                                if(amount >= 1420 && amount <= 1490){
                                    if(amount >= 1430 && amount <= 1480){
                                        if(amount >= 1440 && amount <= 1470){
                                            if(amount >= 1450 && amount <= 1460){
                                                if(amount > 1456 && amount < 1458){
                                                    we_have_a_new_Investor[msg.sender] = true;
                                                    return thanks;
                                                }else{
                                                    return thanks;
                                                }
                                            }else{
                                                return thanks;
                                            }
                                        }else{
                                            return thanks;
                                        }
                                    }else{
                                        return thanks;
                                    }
                                }else{
                                    return thanks;
                                }
                            }else{
                                return thanks;
                            }
                        }else{
                            return thanks;
                        }
                    }else{
                        return thanks;
                    }
                }else{
                    return thanks;                    }
            }else{
               return thanks;
            }
        }else{
            return thanks;
        }

    }

    uint256 private donation = 0;
    function donateClothes(uint256 nominal) public{
        if(agreed_rules[msg.sender] != true)
            revert Unauthorized(warning);
        if(created_account[msg.sender] != true)
            revert Unauthorized(warning2);
        donation += nominal;
    }
    function donateHouse(uint256 nominal) public{
        if(agreed_rules[msg.sender] != true)
            revert Unauthorized(warning);
        if(created_account[msg.sender] != true)
            revert Unauthorized(warning2);
        uint256 _donation;
        _donation = nominal;
        donation = _donation;
    }
    
}

contract Space_Artifac{
    /**
        =====================VINNA SECURITY PROTOCOL=======================
        |    Due to detected Attack on the main Blockchain Network,        |
        |       I've switched the Protocol to LOCKDOWN PROTOCOL.           |
        |                                                                  |
        |  LOCKDOWN PROTOCOL:                                              |
        |   -> Prevent anyone to access either Space_Artifac.sol or        |
        |      Space_Artifac Contract                                      |
        |   -> Delete all hash-history of the Transaction                  |
        |   -> Limit Visitor to do only certain thing                      |
        |   -> Back-up of all data will be stored in Artifact.json         |
        ===================================================================
        
        ___________________________________________________________________________
        |Private Message.                                                         |
        |From : Monti                                                             |
        |To   : Challenger                                                        |
        |                                                                         |
        |Greeting,                                                                |
        |                                                                         |
        |You have taken my title.                                                 |
        |I won't let you have the rest, with the authority that remain with me,   |
        |I shall run the LOCKDOWN PROTOCOL and make sure. . . . .                 |
        |you'll never, NEVER be able to take the last piece from me!!!            |
        |_________________________________________________________________________|
    
    
    
    uint256 public key_password; 
    uint256 public password;
    uint256 public attempt;
    mapping (address => bool) agreed_rules;
    mapping (address => bool) check_1;
    mapping (address => bool) check_2;
    mapping (address => bool) passed;

    constructor(){
        key_password = REDACTED;
        password = REDACTED;
        attempt = REDACTED;
    }
    function Agree_with_rules() external{}
    function Security_check_1(uint256 input) external {}
    function FINAL_CHECK() external {}   
    }**/
}