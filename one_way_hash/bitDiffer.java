
/*
One-way-hash lab by Jens Ekenblad
Date: 19-04-22
*/
import java.math.BigInteger;

class bitDiffer {

    public static void main(String[] args) {

        /*
         * Hash digest from the string: ekenblad@kth.se
         * String h1_md5 = "95646c2f2a223235c034fab5eae01578";
         * String h2_md5 = "b25fc3f069d70bda7952631e4d22916a";
         * String h1_sha256 =
         * "c62ec2f332ad8434f21d6fe939117eb7e9734a4ddc4df274c480fdc2815818c2";
         * String h2_sha256 =
         * "50cf7d40f80443275c54558ca4d09657216da84880765253eadb1faf2e099cbe";
         */

        if (args.length != 2) {
            System.out.println("Incorrect arguments, please enter two valid hash strings");
            System.exit(1);
        }

        if (args[0].length() != args[1].length()) {
            System.out.println("The two hash values are of different lengths, please enter new arguments");
            System.exit(1);
        }

        if (!args[0].matches("(^[a-zA-Z0-9]+$)")) {
            System.out.println("First hash is not valid, please enter string containing [a-z A-Z 0-9]");
            System.exit(1);
        }
        if (!args[1].matches("(^[a-zA-Z0-9]+$)")) {
            System.out.println("Second hash is not valid, please enter string containing [a-z A-Z 0-9]");
            System.exit(1);
        }

        int diffBits = countBits(args[0], args[1]);

        System.out.println("First hash value is: " + args[0]);
        System.out.println("Second hash value is: " + args[1]);
        System.out.println("Number of same bits is: " + diffBits);
    }

    private static int countBits(String a, String b) {

        // convert to BigInteger with hex
        BigInteger h1 = new BigInteger(a, 16);
        BigInteger h2 = new BigInteger(b, 16);

        // bitwise AND of both hash values then count the '1' to get amount that differs
        int bitDiff = h1.xor(h2).bitCount();

        // Input is hex so *4 will return amount of bits in for each character
        return (a.length() * 4) - bitDiff;
    }
}
