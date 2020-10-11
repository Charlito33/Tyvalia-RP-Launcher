package fr.tyvaliarp.launcher;

import java.util.Arrays;

public class NicknameUtils {
    public static String[] autorisedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".split("");


    public static boolean check(String name) {


        for (String character : name.split("")) {
            if (!Arrays.asList(autorisedChars).contains(character)) {
                return false;
            }
        }

        return true;
    }
}
