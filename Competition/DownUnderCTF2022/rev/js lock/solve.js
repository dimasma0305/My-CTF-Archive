async function brute() {
    keyfound = ""
  
    for (var i = 0; i < 100; i++) {
      if (keyfound != "") {
        for (var i = 0; i < keyfound.length; i++) {
          if (keyfound[i] == "1") {
            hit_1()
          }
          if (keyfound[i] == "0") {
            hit_0()
          }
        }
      }
      hit_0()
      hit_0()
      console.log(document.getElementById('status').innerHTML)
      if (document.getElementById('status').innerHTML.includes("Pin 1 is stuck!")) {
        keyfound += "1"
        document.getElementById('status').innerHTML = "foo"
        console.log(keyfound)
        S = {
          current: 1,
          key: '',
          T: LOCK,
          idx: 0
        }
  
  
      } else {
        keyfound += "0"
        document.getElementById('status').innerHTML = "foo"
        console.log(keyfound)
        S = {
          current: 1,
          key: '',
          T: LOCK,
          idx: 0
        }
  
  
      }
    }
  
  }
  brute()