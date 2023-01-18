
/*
One-way-hash lab by Jens Ekenblad
Date: 19-04-22
*/
import java.math.BigInteger;

class bitDiffer {

    public static void main(String[] args) {

        String h1_md5 = "95646c2f2a223235c034fab5eae01578";
        String h2_md5 = "b25fc3f069d70bda7952631e4d22916a";
        String h1_sha256 = "c62ec2f332ad8434f21d6fe939117eb7e9734a4ddc4df274c480fdc2815818c2";
        String h2_sha256 = "50cf7d40f80443275c54558ca4d09657216da84880765253eadb1faf2e099cbe";

        int diff_md5 = countBits(h1_md5, h2_md5);
        int diff_sha256 = countBits(h1_sha256, h2_sha256);

        System.out.println("h1 with MD5 is: " + h1_md5);
        System.out.println("h2 with MD5 is: " + h2_md5);
        System.out.println("h1 with SHA256 is: " + h1_sha256);
        System.out.println("h2 with SHA256 is: " + h2_sha256);

        System.out.println("Number of same bits in md5 is: " + diff_md5);
        System.out.println("Number of same bits in sha256 is: " + diff_sha256);
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
