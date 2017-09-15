package com.xhr.mySnowflakeOid.oid;

import com.xhr.mySnowflakeOid.utils.ContextHolder;
import com.xhr.mySnowflakeOid.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;

/**
 * @author 徐浩然
 * @version RedisIdProvider, 2017-09-14
 */
public class RedisIdProvider implements IOidProvider {

    public static final String IUAP_PRIMARY = "IUAP_PRIMARY";

    public static final String START_VALUE = "START_VALUE";

    private Logger logger = LoggerFactory.getLogger(RedisIdProvider.class);

    private JedisTemplate jt;

    public JedisTemplate getJt() {
        return jt;
    }

    public void setJt(JedisTemplate jt) {
        this.jt = jt;
    }

    @Override
    public String generatorID(String module) {
        if (jt == null)
            jt = ContextHolder.getContext().getBean(JedisTemplate.class);
        if (jt == null) {
            logger.error("redis config is error! please init ContextHolder!");
            return null;
        }

        String result = null;

        // 根据module创建或者获取reids的key，对应的value自增
        String key = IUAP_PRIMARY + "_" + module;


        if (jt.get(key) == null) {
            long initId = 1L;

            String startValueKey = key + "_" + START_VALUE;
            String settingStartValue = PropertyUtil.getPropertyByKey(startValueKey);
            if (settingStartValue != null && StringUtils.isNotBlank(settingStartValue)) {
                initId = Long.parseLong(settingStartValue.trim());
                logger.info("getting start value from config file,init value is {}.", new Object[]{settingStartValue});
            }

            if (initId == 1L) {
                logger.info("init redis key with default value, default value is {}.", initId);
            }

            // 设置初始值,初始值先从配置文件中读取，如果没有，采用默认值
            jt.set(key, String.valueOf(initId));
            result = String.valueOf(initId);
        } else {
            result = String.valueOf(jt.incr(key));
        }

        logger.info("get object primary key from redis, key value is {}." + result);
        return result;
    }

}

