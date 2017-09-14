package com.xhr.mySnowflakeOid.utils;

import com.xhr.mySnowflakeOid.oid.IOidProvider;

/**
 * @author 徐浩然
 * @version IDGenerator, 2017-09-14
 */
public class IDGenerator
{

    public static IOidProvider idProvider;

    public static String generateObjectID(String module) {

        if (idProvider == null) {
            synchronized (IDGenerator.class) {
                if (idProvider == null) {
                    idProvider = IdProviderFactory.getIdProvider();
                }
            }
        }

        return idProvider.generatorID(module);

    }
}
