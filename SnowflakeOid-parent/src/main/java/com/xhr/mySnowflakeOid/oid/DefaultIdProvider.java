package com.xhr.mySnowflakeOid.oid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author 徐浩然
 * @version DefaultIdProvider, 2017-09-07
 */
public class DefaultIdProvider implements IOidProvider {

    private Logger logger = LoggerFactory.getLogger(DefaultIdProvider.class);

    @Override
    public String generatorID(String module) {
        String result = UUID.randomUUID().toString();

        logger.info("get primary key form uuid, the value is {}.", result);

        return result;
    }

}
