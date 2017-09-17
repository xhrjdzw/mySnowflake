package com.xhr.mySnowflakeOid.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author 徐浩然
 * @version SerializUtil, 2017-09-17
 */
public class SerializUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializUtil.class);

    public SerializUtil() {
    }

    public static String getStrFromObj(Object obj) {
        String serStr = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = URLEncoder.encode(serStr, "UTF-8");
        } catch (IOException var17) {
            LOGGER.error(var17.getMessage(), var17);
        } finally {
            try {
                if(objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException var16) {
                LOGGER.error(var16.getMessage(), var16);
            }

            try {
                if(byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException var15) {
                LOGGER.error(var15.getMessage(), var15);
            }

        }

        return serStr;
    }

    public static Object getObjectFromStr(String serStr) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object obj = null;

        try {
            String e = URLDecoder.decode(serStr, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(e.getBytes("ISO-8859-1"));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception var17) {
            LOGGER.error(var17.getMessage(), var17);
        } finally {
            try {
                if(objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException var16) {
                LOGGER.error(var16.getMessage(), var16);
            }

            try {
                if(byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException var15) {
                LOGGER.error(var15.getMessage(), var15);
            }

        }

        return obj;
    }

    public static Object byteToObject(byte[] bytes) {
        Object obj = null;

        try {
            ByteArrayInputStream e = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(e);
            obj = oi.readObject();
            e.close();
            oi.close();
        } catch (Exception var4) {
            LOGGER.error("translation:" + var4.getMessage(), var4);
        }

        return obj;
    }

    public static byte[] objectToByte(Object obj) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(e);
            oo.writeObject(obj);
            bytes = e.toByteArray();
            e.close();
            oo.close();
        } catch (Exception var4) {
            LOGGER.error("translation:" + var4.getMessage(), var4);
        }

        return bytes;
    }
}
