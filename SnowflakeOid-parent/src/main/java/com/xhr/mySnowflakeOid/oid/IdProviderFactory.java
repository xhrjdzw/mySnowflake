package com.xhr.mySnowflakeOid.oid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xhr.mySnowflakeOid.utils.PropertyUtil
/**
 * @author 徐浩然
 * @version IdProviderFactory, 2017-09-14
 */
public class IdProviderFactory
{
    public static final String ID_PROVIDER_TYPE = "idtype";
    public static final String ID_PROVIDER_CLASS = "idproviderclass";

    public static final String IDTYPE_UUID = "uuid";
    public static final String IDTYPE_REDIS = "redis";
    public static final String IDTYPE_SNOWFLAKE = "snowflake";
    public static final String IDTYPE_UAPOID = "uapoid";

    private static Logger logger = LoggerFactory.getLogger(IdProviderFactory.class);

    public static IOidProvider getIdProvider() {

        String idProviderClass = PropertyUtil.getPropertyByKey(ID_PROVIDER_CLASS);
        String genIdType = PropertyUtil.getPropertyByKey(ID_PROVIDER_TYPE);
        IOidProvider provider = findProvider(idProviderClass, genIdType);

        return provider;
    }

    public static IOidProvider getIdProvider(String extenstionType, String providerType) {
        IOidProvider provider = findProvider(extenstionType, providerType);

        return provider;
    }

    private static IOidProvider findProvider(String idProviderClass, String genIdType) {
        IOidProvider provider = null;
        if (StringUtils.isNotBlank(idProviderClass)) {
            try {
                provider = (IOidProvider) Class.forName(idProviderClass).newInstance();
            } catch (Exception e) {
                logger.error("create custom IOidProvider error! please check config file!", e);
            }
        } else {
            if (IDTYPE_UUID.equalsIgnoreCase(genIdType) || StringUtils.isBlank(genIdType)) {
                provider = new DefaultIdProvider();
            } else if (IDTYPE_SNOWFLAKE.equalsIgnoreCase(genIdType)) {
                provider = new SnowFlakeProvider();
            } else if (IDTYPE_REDIS.equalsIgnoreCase(genIdType)) {
                provider = new RedisIdProvider();
            } else if (IDTYPE_UAPOID.equalsIgnoreCase(genIdType)) {
                provider = new UapIdProvider();
            }
        }

        logger.info("get IOidProvider by factory! the provider class is {}.", (null == provider ? null : provider.getClass().getName()));
        return provider;
    }
}
