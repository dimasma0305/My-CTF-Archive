import java.util.*;

class solve {
    public static void main(String args[]) {
        solve vaultDoor = new solve();
        vaultDoor.checkPassword("asd");
    }
    public boolean checkPassword(String password) {
        String input = "picoCTF{"+"w4rm1ng_Up_w1tH_jAv4_be8d9806f18"+"}\n";
        System.out.print(input);
        return password.equals("w4rm1ng_Up_w1tH_jAv4_be8d9806f18");
    }
}
