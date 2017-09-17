package com.xhr.mySnowflakeOid.other;

import org.springside.modules.nosql.redis.pool.JedisPool;
import org.springside.modules.nosql.redis.pool.JedisPoolBuilder;

import java.util.List;

/**
 * @author 徐浩然
 * @version RedisPoolFactory, 2017-09-17
 */

public class RedisPoolFactory {
    public RedisPoolFactory() {
    }

    public static JedisPool createJedisPool(String url) {
        return (new JedisPoolBuilder()).setUrl(url).buildPool();
    }

    public static List<JedisPool> createShardedJedisPools(String shardedUrl) {
        return (new JedisPoolBuilder()).setUrl(shardedUrl).buildShardedPools();
    }
}