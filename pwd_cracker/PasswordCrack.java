/*

PasswordCracker assignement made by : Jens Ekenblad
15-05-2022
ekenblad@kth.se

 */


import java.io.*;
import java.util.ArrayList;
import java.util.Locale;


public class PasswordCrack {

    public static final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};


    public static void main(String[] args) throws IOException {


        if (args.length != 2) {
            System.out.println("Please enter two valid arguments (filename, filename)");
            System.exit(1);
        }

        BufferedReader inputDict = null;
        BufferedReader inputPwd = null;

        // Check for arg0
        try {
            inputDict = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        } catch (Exception e) {
            System.out.println("Argument #1 input file doesnt exist or is invalid, please provide a valid file");
            System.exit(1);
        }

        // Check for arg1
        try {
            inputPwd = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
        } catch (Exception e) {
            System.out.println("Argument #2 input file doesnt exist or is invalid, please provide a valid file");
            System.exit(1);
        }


        String user;
        ArrayList<String> hashPwd = new ArrayList<>();

        // Run through all users and add their hashed pwd to a list and same time check their names against stored hash
        while ((user = inputPwd.readLine()) != null) {


            String[] userData;
            String salt;
            String hash;
            String[] name;
            String firstName;
            String lastName;


            userData = user.split(":");
            salt = userData[1].substring(0, 2);
            hash = userData[1];
            name = userData[4].split(" ");
            firstName = userData[0];
            hashPwd.add(userData[1]);

            if (name.length == 3) {
                lastName = name[2];
            } else {
                lastName = name[1];
            }

            ArrayList<String> mngFirst = mangleCheck(firstName, 1);
            ArrayList<String> mngLast = mangleCheck(lastName, 1);
            ArrayList<String> mngName = mangleCheck(firstName + lastName, 1);

            for (int i = 0; i < mngFirst.size(); i++) {

                if (jcrypt.crypt(salt, mngFirst.get(i)).equals(hash)) {
                    System.out.println(mngFirst.get(i));
                    hashPwd.remove(hashPwd.size() - 1);
                    break;
                }
                if (jcrypt.crypt(salt, mngLast.get(i)).equals(hash)) {
                    System.out.println(mngLast.get(i));
                    hashPwd.remove(hashPwd.size() - 1);
                    break;
                }
                if (jcrypt.crypt(salt, mngName.get(i)).equals(hash)) {
                    System.out.println(mngName.get(i));
                    hashPwd.remove(hashPwd.size() - 1);
                    break;
                }
            }
        }

        ArrayList<String> SmangledColl = new ArrayList<>();
        String dictWord;
        String oldWord = "";
        // Plain word and single mangle check
        while ((dictWord = inputDict.readLine()) != null && !hashPwd.isEmpty()) {

            // Only care about first 8 characters and skip words that are of same regex
            if (dictWord.length() > 8) {
                dictWord = dictWord.substring(0, 8);
            }
            if (!oldWord.equals(dictWord)) {
                oldWord = dictWord;

/*
                // Check for plain word
                for(int k = 0; k < hashPwd.size(); k++) {

                    if(jcrypt.crypt(hashPwd.get(k).substring(0,2), dictWord).equals(hashPwd.get(k))) {
                        System.out.println(dictWord);
                        hashPwd.remove(k);
                    }

                }*/

                // Create all mangles of each dict word and add it two dubbel list
                ArrayList<String> SmangledDict = mangleCheck(dictWord, 1);
                SmangledColl.addAll(SmangledDict);

                // Check for mangled word
                for (int i = 0; i < SmangledDict.size(); i++) {


                    for (int j = 0; j < hashPwd.size(); j++) {

                        if (jcrypt.crypt(hashPwd.get(j).substring(0, 2), SmangledDict.get(i)).equals(hashPwd.get(j))) {
                            System.out.println(SmangledDict.get(i));
                            hashPwd.remove(j);
                        }
                    }
                }
            }
        }

        // Double mangle on all single mangles on rest of users
        for (int i = 0; i < SmangledColl.size(); i++) {

            ArrayList<String> DoublemangledDict = mangleCheck(SmangledColl.get(i), 2);

            for (int k = 0; k < DoublemangledDict.size(); k++) {


                for (int j = 0; j < hashPwd.size(); j++) {

                    if (jcrypt.crypt(hashPwd.get(j).substring(0, 2), DoublemangledDict.get(k)).equals(hashPwd.get(j))) {
                        System.out.println(DoublemangledDict.get(k));
                        hashPwd.remove(j);
                    }
                }
            }
            if(hashPwd.isEmpty())
                break;
        }

    }


    // Function to mangle words in 12 different ways
    public static ArrayList<String> mangleCheck(String word, int type) {

        ArrayList<String> mangles = new ArrayList<>();

        String revWord = "";

        if (type == 1) {

            // Just plain word
            mangles.add(word);

            // prepend/append letter/number

            for (int j = 0; j < letters.length; j++) {

                mangles.add(letters[j] + word);
                mangles.add(word + letters[j]);
            }

            for (int j = 0; j < numbers.length; j++) {

                mangles.add(numbers[j] + word);
                mangles.add(word + numbers[j]);
            }
        }

        // delete first
        mangles.add(word.substring(1));


        // delete last
        mangles.add(word.substring(0, word.length() - 1));

        // reverse
        for (int j = word.length() - 1; j >= 0; j--) {
            revWord += word.charAt(j);
        }
        mangles.add(revWord);


        // duplicate
        mangles.add(word + word);


        // reflect
        mangles.add(word + revWord);
        mangles.add(revWord + word);


        // uppercase
        mangles.add(word.toUpperCase(Locale.ROOT));


        // lowercase
        mangles.add(word.toLowerCase(Locale.ROOT));


        // capitalize
        char first = word.charAt(0);
        StringBuilder str = new StringBuilder();
        mangles.add(str.append(first).toString().toUpperCase(Locale.ROOT) + word.substring(1));


        // ncapitalize
        mangles.add(first + word.substring(1).toUpperCase(Locale.ROOT));


        // toggle
        StringBuilder togFirst = new StringBuilder();
        StringBuilder togSecond = new StringBuilder();

        for (int j = 0; j < word.length(); j++) {

            if (j == 0) {
                togFirst.append(Character.toUpperCase(word.charAt(j)));
            } else {
                if (Character.isUpperCase(togFirst.charAt(j - 1))) {
                    togFirst.append(Character.toLowerCase(word.charAt(j)));
                } else {
                    togFirst.append(Character.toUpperCase(word.charAt(j)));
                }
            }

            if (j == 0) {
                togSecond.append(Character.toLowerCase(word.charAt(j)));
            } else {
                if (Character.isLowerCase(togSecond.charAt(j - 1))) {
                    togSecond.append(Character.toUpperCase(word.charAt(j)));
                } else {
                    togSecond.append(Character.toLowerCase(word.charAt(j)));
                }
            }
        }
        mangles.add(togFirst.toString());
        mangles.add(togSecond.toString());


        // l33t swap
        char[] array = word.toCharArray();
        for (int j = 0; j < array.length; j++) {
            switch (array[j]) {
                case 'o':
                    array[j] = '0';
                    break;
                case 'l':
                    array[j] = '1';
                    break;

                case 'e':
                    array[j] = '3';
                    break;

                case 'a':
                    array[j] = '4';
                    break;

                case 't':
                    array[j] = '7';
                    break;
            }
        }
        mangles.add(array.toString());


        return mangles;
    }


}


