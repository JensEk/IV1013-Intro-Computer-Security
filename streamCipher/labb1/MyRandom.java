import java.math.BigInteger;
import java.util.Random;


class MyRandom extends Random {

    private long seed;

    /* task2
    int m = 50021;
    int[] a = {2, 130, 833, 2113, 3522, 8129, 19009, 33409, 40513, 49665, 49986};
    //int a = 833;
    */

    // task3
    int[] S = new int[256];
    int i = 0;
    int j = 0;


    public MyRandom() {
        setSeed(1);
    }

    public MyRandom(long seed) {
        setSeed(seed);
    }

    // task3
    public MyRandom(byte[] key) {

        for(int i = 0; i < 256; i++) {
            S[i] = i;

        }

        int j_ = 0;
        for(int i = 0; i < 256; i++) {
            j_ = ((j_ + S[i] + key[i % key.length]) % 256);
            swapValues( i, j_);
        }
    }

    @Override
    public int next(int bits) {

        /* task2
        int a_ = a[(int)this.seed%a.length];
        long x = ((a_*this.seed) % m);
        setSeed(x);
        double r = (double)x / m;

        return ((1 << bits) -1) & (int) Math.floor(((1 << bits) * r) + 1);
        */

        // task3
        i = ((i + 1) % 256);
        j = ((j + S[i]) % 256);

        swapValues(i, j);

        return (S[(S[i] + S[j]) % 256]);

    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    // task3
    private void swapValues(int i, int j) {
        int temp = S[i];
        S[i] = S[j];
        S[j] = temp;
    }



}
