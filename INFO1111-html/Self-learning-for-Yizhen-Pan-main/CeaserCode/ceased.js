function encrypt(){ // for encrypt
    let str = document.getElementById("message").value;
    let rt = document.getElementById("rot").valueAsNumber;
    let new_str = "";
    for (let i = 0; i < str.length; i++) {
        if(str.charCodeAt(i)>=65 && str.charCodeAt(i)<=90){// this is the rotation part for letters in lower case
            new_str+=String.fromCharCode((str.charCodeAt(i)- 65 + rt + 26) % 26 + 65);
        }else if(str.charCodeAt(i)>=97 && str.charCodeAt(i)<=122){// this is the rotation part for letters in upper case
            new_str+=String.fromCharCode((str.charCodeAt(i)- 97 + rt + 26) % 26 + 97);
        }else{
            new_str+=String.fromCharCode(str.charCodeAt(i)); // for any marks like space or commas, stay same.
        }
    }
     document.getElementById("result").innerHTML = new_str; //output the encrypted message
}

function decrypt() {  //this is used to decrypt the message,
                      // it is a revert version of encrypt since it do everything backward
    let str = document.getElementById("message").value;
    let rt = document.getElementById("rot").valueAsNumber;
    let new_str = "";
    for (let i = 0; i < str.length; i++) {
        if(str.charCodeAt(i)>=65 && str.charCodeAt(i)<=90){ //for lower case decrypt
            new_str+=String.fromCharCode((str.charCodeAt(i)- 65 - rt + 26) % 26 + 65);
        }else if(str.charCodeAt(i)>=97 && str.charCodeAt(i)<=122){ //for upper case decrypt
            new_str+=String.fromCharCode((str.charCodeAt(i)- 97 - rt + 26) % 26 + 97);
        }else{
            new_str+=String.fromCharCode(str.charCodeAt(i));
        }
    }
    document.getElementById("result").innerHTML = new_str;
}