/*
 * EE422C Final Project submission by
 * Jacob Marquardt
 * jgm3339
 * 17150
 * Slip days used: <1>
 * Spring 2023
 */

package src;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Login {
    private final HashMap<String, String> usersAndPasswords;

    protected Login() {
        usersAndPasswords = new HashMap<>();
        try {
            Scanner reader = new Scanner(new File("logins.txt"));
            while (reader.hasNextLine()){
                String [] input = reader.nextLine().split(",");
                usersAndPasswords.put(input[0], input[1]);
            }
        } catch (FileNotFoundException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    protected Boolean tryLogin(String user, String password) {
        if (usersAndPasswords.containsKey(user))
            return usersAndPasswords.get(user).equals(password);
        return false;
    }
}
