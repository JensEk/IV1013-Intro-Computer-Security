/*
Hidden encryption by: Jens Ekenblad
Solving task 1 and 3
ekenblad@kth.se
Date: 29-05-2022
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

public class Hiddec
{

    public static void main(String[] args) throws IOException {

        String keyString = "";
        String ctrString = "";
        byte[] key = null;
        byte[] keyHash = null;
        byte[] counter = null;
        byte[] file = null;
        BufferedInputStream inputData = null;
        BufferedReader inputKey = null;
        BufferedReader inputCTR = null;
        BufferedOutputStream outputData = null;
        Cipher cipher;

        // Extract all arguments
        for(String arg : args) {

            String[] pair = arg.split("=");

            switch (pair[0]) {

                case "--key": {

                    try {

                        if (!pair[1].contains(".")) {
                            keyString = pair[1];
                        } else {

                            inputKey = new BufferedReader(new InputStreamReader(new FileInputStream(pair[1])));
                            String read;
                            while((read = inputKey.readLine()) != null) {
                                keyString += read;
                            }
                        }


                        // inspo from https://www.geeksforgeeks.org/java-program-to-convert-hex-string-to-byte-array/
                        key = new byte[keyString.length() / 2];
                        for(int i = 0; i < keyString.length(); i+=2) {
                            key[i / 2] = (byte) ((Character.digit(keyString.charAt(i), 16) << 4) + Character.digit(keyString.charAt(i+1), 16));
                        }

                        keyHash = MessageDigest.getInstance("MD5").digest(key);

                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println(" Key argument doesnt exist or is invalid, please provide a valid file or string");
                        System.exit(1);
                    }
                    break;
                }

                case "--ctr": {
                    try {

                        if (!pair[1].contains(".")) {
                            ctrString = pair[1];
                        } else {

                            inputCTR = new BufferedReader(new InputStreamReader(new FileInputStream(pair[1])));
                            String read;
                            while((read = inputCTR.readLine()) != null) {
                                ctrString += read;
                            }
                        }
                        // inspo from https://www.geeksforgeeks.org/java-program-to-convert-hex-string-to-byte-array/
                        counter = new byte[ctrString.length() / 2];
                        for(int i = 0; i < ctrString.length(); i+=2) {
                            counter[i / 2] = (byte) ((Character.digit(ctrString.charAt(i), 16) << 4) + Character.digit(ctrString.charAt(i+1), 16));
                        }

                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("CTR argument doesnt exist or is invalid, please provide a valid file or string");
                        System.exit(1);
                    }
                    break;
                }

                case "--input": {
                    try {
                        file = Files.readAllBytes(Paths.get(pair[1]));
                        inputData = new BufferedInputStream(new FileInputStream(pair[1]));
                        if(file.length == 0) {
                            System.out.println("Input file is empty, please enter a new one");
                        }
                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Data argument file doesnt exist or is invalid, please provide a valid file");
                        System.exit(1);
                    }
                    break;
                }

                case "--output": {
                    try {
                        outputData = new BufferedOutputStream(new FileOutputStream(pair[1]));
                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Argument output file is invalid, please enter a correct filename");
                        System.exit(1);
                    }
                    break;
                }
            }
        }

        try {

            byte[] scanned = null;
            byte[] dataCheck = null;
            int countBytes = 0;
            int start = -1;
            int end = 0;



            // ECB decrypt
            if(counter == null)  {
                SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec);

                while((scanned = inputData.readNBytes(16)) != null) {

                    countBytes += 16;
                    if(Arrays.equals(cipher.update(scanned), keyHash)) {

                        start = countBytes;
                        while((scanned = inputData.readNBytes(16)) != null) {

                            if(Arrays.equals(cipher.update(scanned), keyHash)) {
                                end = countBytes;
                                dataCheck = cipher.update(inputData.readNBytes(16));
                                break;
                            }
                            countBytes += 16;
                        }
                        break;
                    }
                }
                inputData.close();

                byte[] decryptedData = Arrays.copyOfRange(cipher.update(file), start, end);
                byte[] decryptedDataHash = MessageDigest.getInstance("MD5").digest(decryptedData);

                if(Arrays.equals(dataCheck,decryptedDataHash)) {
                    outputData.write(decryptedData, 0, decryptedData.length);
                    outputData.flush();
                } else {
                    System.out.println("Data found and hashed doesnt match the H(KEY) value");
                    System.exit(1);
                }


            } // CTR decrypt
            else {
                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                byte[] decryptedData = null;
                byte[] sector = null;
                int incr = 0;


                while(end == 0) {
                    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(counter));

                    decryptedData = cipher.update(Arrays.copyOfRange(file, incr, file.length));

                    if(start < 0) {
                        for(int i = 0; i < decryptedData.length; i+=16) {
                            sector = Arrays.copyOfRange(decryptedData, i, i+16);
                            if(Arrays.equals(sector, keyHash)){
                                start = i;
                                break;
                            }
                        }
                    }

                    if(start >= 0) {
                        for(int i = start+keyHash.length; i < decryptedData.length; i+=16) {
                            sector = Arrays.copyOfRange(decryptedData, i, i+16);
                            if(Arrays.equals(sector, keyHash)){
                                end = i;
                                dataCheck = Arrays.copyOfRange(decryptedData, i+16, i+(2*16));
                                break;
                            }
                        }
                    }

                    incr++;
                    if(incr == file.length){
                        System.out.println("No match of keys and hash");
                        System.exit(1);
                    }
                }

                byte[] finalDecryptedData = Arrays.copyOfRange(decryptedData, start+16, end);
                byte[] decryptedDataHash = MessageDigest.getInstance("MD5").digest(finalDecryptedData);

               if(Arrays.equals(dataCheck,decryptedDataHash)) {
                    outputData.write(finalDecryptedData, 0, finalDecryptedData.length);
                    outputData.flush();
                } else {
                   System.out.println("Data found and hashed doesnt match the H(KEY) value");
                   System.exit(1);
               }
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Issue scanning the file");
            System.exit(1);
        }
    }
}

