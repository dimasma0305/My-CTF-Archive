use sha256::digest;

fn main() {
    for i in 1..4228250625 as u32 {
        let val = digest(format!("{}", i.to_string()));
        let trunc = &val[val.len() - 8..];
        // println!("{}\r", trunc);
        if trunc == "00000000" {
            println!("{}", i);
            break;
        }
    }

}
