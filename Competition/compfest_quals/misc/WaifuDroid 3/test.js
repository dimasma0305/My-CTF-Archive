const isValid = (str) => {
    if(/[\+\-\/~\[\]\{\}!]+$/.test(str)) {
        return true;
    } 
	return false;
};


let content = "{`yes Flag`}";

let response = `the response`;

if(content.length > 766 || !isValid(content)) {
    console.log(response);
}

try {
    content = eval(content);
} catch(err) {
    content = ``;
}

if(content === `yes Flag`) {
    response = `secret`;
}

console.log(response);