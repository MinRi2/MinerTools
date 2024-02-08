package utils;

import java.math.*;
import java.nio.charset.*;
import java.security.*;

/**
 * @author minri2
 * Create by 2024/2/8
 */
public class MD5{
    private static MessageDigest md5;

    public static void initMD5(){
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public static String md5(String string){
        if(md5 == null){
            initMD5();
        }

        md5.update(string.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, md5.digest()).toString(16);
    }
}
