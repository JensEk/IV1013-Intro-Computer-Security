/*
Hidden encryption by: Jens Ekenblad
Solving task 2 and 4
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
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Hidenc
{

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String keyString = "";
        String ctrString = "";
        String offsetString = "";
        byte[] key = null;
        byte[] keyHash = null;
        byte[] counter = null;
        byte[] file = null;
        BufferedInputStream inputData = null;
        BufferedReader inputKey = null;
        BufferedReader inputCTR = null;
        BufferedReader inputOff = null;
        BufferedOutputStream outputData = null;
        Cipher cipher;
        int offset = -1;
        byte[] template = null;
        int size = 0;
        byte[] blobData = null;
        byte[] blobDataHashed = null;
        byte[] encryptedBlob = null;
        Random rand;

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
                        blobData = Files.readAllBytes(Paths.get(pair[1]));
                       // inputData = new BufferedInputStream(new FileInputStream(pair[1]));
                        if(blobData.length == 0) {
                            System.out.println("Input file is empty, please enter a new one");
                            System.exit(1);
                        }
                        blobDataHashed = MessageDigest.getInstance("MD5").digest(blobData);
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

                case "--offset": {
                    try {

                        if (!pair[1].contains(".")) {
                            offset = Integer.parseInt(pair[1]);
                        } else {
                            inputOff = new BufferedReader(new InputStreamReader(new FileInputStream(pair[1])));
                            String read;
                            while((read = inputOff.readLine()) != null) {
                                offsetString += read;
                            }
                            offset = Integer.parseInt(offsetString);
                        }


                        if(offset < 0) {
                            System.out.println("Please enter valid offset value >= 0");
                            System.exit(1);
                        }
                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Argument offset is invalid, please enter a correct value");
                        System.exit(1);
                    }
                    break;
                }

                case "--template": {
                    try {
                        template = Files.readAllBytes(Paths.get(pair[1]));
                        if(template.length == 0) {
                            System.out.println("Template file is empty, please enter a new one");
                        }

                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Argument template file is invalid, please enter a correct filename");
                        System.exit(1);
                    }
                    break;
                }

                case "--size": {
                    try {
                        size = Integer.parseInt(pair[1]);
                        if(size < 1) {
                            System.out.println("Please enter valid size value > 0");
                            System.exit(1);
                        }
                    }catch (Exception e) {
                        System.out.println(e);
                        System.out.println("Argument size is invalid, please enter a correct value");
                        System.exit(1);
                    }
                    break;
                }
            }
        }

        // Check if both template and size have been given as arguments else randomly fill a template with data
        if (template != null && size > 0){
            System.out.println("Just one of template or size");
            System.exit(1);
        }
        if(size > 0) {
            rand = new Random();
            template = new byte[size];
            rand.nextBytes(template);
        }

        // Create the blob with [key - data - key - hashData]
        byte[] blob = new byte[keyHash.length + blobData.length + keyHash.length + blobDataHashed.length];
        int index = 0;
        for(byte data : keyHash) {
            blob[index] = data;
            index++;
        }
        for(byte data : blobData) {
            blob[index] = data;
            index++;
        }
        for(byte data : keyHash) {
            blob[index] = data;
            index++;
        }
        for(byte data : blobDataHashed) {
            blob[index] = data;
            index++;
        }

        // If no offset is given it should be randomized and fit into the template
        if(offset < 0) {
            rand = new Random();
            if(blob.length/16 == template.length/16) {
                offset = 0;
            }else {
                offset = rand.nextInt(((template.length/16) - (blob.length/16))*16 );
            }
        }else {
            if((blob.length + offset) > template.length) {
                System.out.println("Blob and offset is out of boundary of template by: " + ((blob.length + offset) - template.length) + " bytes");
                System.exit(1);
            }
        }


        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            if(counter == null) {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            }else {
                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(counter));
            }
            encryptedBlob = cipher.doFinal(blob);

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Issue encrypting the blob, please verify inputs for it");
            System.exit(1);
        }



        try {

            for(int i = 0; i < blob.length; i++) {
                template[offset+i] = encryptedBlob[i];
            }

            outputData.write(template, 0, template.length);
            outputData.flush();
        }catch (Exception e) {
            System.out.println(e);
            System.out.println("Error writing template to outputfile try again");
        }

    }
}


