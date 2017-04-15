package com.github.davidmoten.rtree.kv.store.redis;

import java.io.*;
import java.util.Base64;

/**
 *
 * Created by mgh on 4/14/17.
 */
public final class RedisUtil {

    private RedisUtil(){}

    public final static String DATA_COUNT= "data count";

    public final static String NODE_COUNT = "node count";

    public final static String CONTEXT = "context";

    public final static String SIZE = "size";

    public final static String ROOT_NAME = "root";

    private final static String DELIMINATOR = ":";

    public static String redisKey(String... keyParts){
        String res = "";
        for (int i = 0; i < keyParts.length; i++){
            res += keyParts[i];
            if (i != keyParts.length - 1)
                res += DELIMINATOR;
        }
        return res;
    }

    /** Read the object from Base64 string. */
    public static Object fromString( String s ) {
        byte [] data = Base64.getDecoder().decode( s );
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /** Write the object to a Base64 string. */
    public static String toString( Serializable o ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
