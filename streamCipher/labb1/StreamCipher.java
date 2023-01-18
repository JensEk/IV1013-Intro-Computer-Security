import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;


public class StreamCipher {

    public static void main(String[] args) throws IOException {

        if(args.length != 3) {
            System.out.println("Please enter three valid arguments (integer, filename, filename)");
            System.exit(1);
        }

        BufferedInputStream inputReader = null;
        BufferedOutputStream outputWriter = null;


        // Check for arg0
        try {
            Long.parseLong(args[0]);
        }catch (Exception e) {
            System.out.println("Argument #1 key should only consist of integers [0-9], please try again");
            System.exit(1);
        }

        // Check for arg1
        try {
            inputReader = new BufferedInputStream(new FileInputStream(args[1]));
        }catch (Exception e) {
            System.out.println("Argument #2 input file doesnt exist or is invalid, please provide a valid file");
            System.exit(1);
        }

        // Check for arg2
        try {
            outputWriter = new BufferedOutputStream(new FileOutputStream(args[2]));
        }catch (Exception e) {
            System.out.println("Argument #3 output file is invalid, please enter a correct filename");
            System.exit(1);
        }


        // Read all bytes from inputfile and create outputfile of same size
        byte[] infile = inputReader.readAllBytes();
        inputReader.close();
        if(infile.length == 0) {
            System.out.println("Empty input file, please provide a new one");
            System.exit(1);
        }
        byte[] outfile = new byte[infile.length];
        String inputKey = args[0];
        BigInteger keyValue = new BigInteger(inputKey);
        if(keyValue.intValue() < 0) {
            System.out.println("Key must be a positive number sequence");
            System.exit(1);
        }

        try {

            /* task 1 och 2
            //Random prng = new Random(Long.parseLong(args[0]));
            MyRandom prng = new MyRandom(Long.parseLong(args[0]));

            for(int i = 0; i < outfile.length; i++) {
                outfile[i] = (byte) (infile[i]^prng.nextInt(256));
            }*/


            //task3
            byte[] key = keyValue.toByteArray();
            MyRandom prng = new MyRandom(key);


            for(int i = 0; i < outfile.length; i++) {
                outfile[i] =  (byte)(infile[i]^prng.next(8));
            }


            outputWriter.write(outfile);
            outputWriter.flush();
            System.out.println("Program finished without error");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Runtime error while encrypting/decrypting, please try again");
            e.printStackTrace();
            System.exit(1);
        }










    }
}
