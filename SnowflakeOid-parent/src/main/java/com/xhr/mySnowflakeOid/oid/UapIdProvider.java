package com.xhr.mySnowflakeOid.oid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 徐浩然
 * @version UapIdProvider, 2017-09-14
 */
public class UapIdProvider implements IOidProvider {

    private Logger logger = LoggerFactory.getLogger(UapIdProvider.class);

    @Override
    public String generatorID(String schemaCode) {
        String result = UapOidGenerator.getInstance().genOid(schemaCode);
        logger.info("get primary key form uapoid, the value is {}.", result);
        return result;
    }

}
