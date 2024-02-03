package root.unitech.utils;


import java.util.Random;
import java.util.UUID;

public class RandomAccountGenerator {

    public static String randomAccountNumberGenerator(){
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
}
