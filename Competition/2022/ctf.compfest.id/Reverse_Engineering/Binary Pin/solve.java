public class solve {
    public static void main(String[] args) {
        // bruteforce binarypin
        // declare variables
        System.out.println("Bruteforce binarypin");
        for (int i = 0; i < 256; i++) {
            String binary = Integer.toBinaryString(i);
            while (binary.length() < 8) {
                binary = "0" + binary;
            }
            Secret.getInstance().process(binary.charAt(0));
            Secret.getInstance().process(binary.charAt(1));
            Secret.getInstance().process(binary.charAt(2));
            Secret.getInstance().process(binary.charAt(3));
            Secret.getInstance().process(binary.charAt(4));
            Secret.getInstance().process(binary.charAt(5));
            Secret.getInstance().process(binary.charAt(6));
            Secret.getInstance().process(binary.charAt(7));
            System.out.println(Secret.getInstance().getData());
            Secret.getInstance().resetInstance();
        }
    }
}
