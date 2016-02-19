package milestone2.chord;

import org.apache.commons.codec.digest.DigestUtils;


public class Key {

    public static long generate64BitsKey(String host, int port) {return generate64BitsKey(host + port);}
    public static long generate64BitsKey(String key)
    {
        String long_key = DigestUtils.sha1Hex(key);
        String short_key = long_key.substring(0, 16); // length
        return Long.parseLong(short_key, 16); // base

    };
    public static int  generate32BitsKey(String host, int port) {return generate32BitsKey(host + port);}
    public static int generate32BitsKey(String key)
    {
        String long_key = DigestUtils.sha1Hex(key);
        String short_key = long_key.substring(0, 7);
        return Integer.parseInt(short_key, 16);
    };
    
    public static int  generate16BitsKey(String host, int port) {return generate16BitsKey(host + port);}
    public static int generate16BitsKey(String key)
    {
        String long_key = DigestUtils.sha1Hex(key);
        String short_key = long_key.substring(0, 4);
        return Integer.parseInt(short_key, 16);
    };

}
