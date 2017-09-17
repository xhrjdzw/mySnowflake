package com.xhr.mySnowflakeOid.other;

import org.springside.modules.nosql.redis.JedisTemplate;
import redis.clients.jedis.exceptions.JedisException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 徐浩然
 * @version ICacheManager, 2017-09-17
 */
public interface ICacheManager {
    <T extends Serializable> void set(String var1, T var2);

    <T extends Serializable> void setex(String var1, T var2, int var3);

    void expire(String var1, int var2);

    <T extends Serializable> void setAndExpireInPipeline(String var1, T var2, int var3);

    void piplineExecute(JedisTemplate.PipelineActionNoResult var1);

    <T> List<T> piplineExecute(JedisTemplate.PipelineAction var1);

    Boolean exists(String var1);

    <T extends Serializable> Serializable get(String var1);

    <T extends Serializable> T hget(String var1, String var2);

    <T extends Serializable> List<T> hmget(String var1, String... var2);

    Boolean hexists(String var1, String var2);

    Map<byte[], byte[]> hgetAll(String var1);

    <T extends Serializable> void hset(String var1, String var2, T var3);

    <T extends Serializable> void hmset(String var1, Map<String, T> var2);

    void removeCache(String var1);

    void hdel(String var1, String var2);

    void hdel(String var1, String... var2);

    void initNumForIncr(String var1, long var2);

    Long incr(String var1);

    Long decr(String var1);

    <T> T execute(JedisTemplate.JedisAction<T> var1) throws JedisException;

    void execute(JedisTemplate.JedisActionNoResult var1) throws JedisException;

    void execute(JedisTemplate.PipelineActionNoResult var1) throws JedisException;

    List<Object> execute(JedisTemplate.PipelineAction var1) throws JedisException;

    Serializer getSerializer();

    void setSerializer(Serializer var1);
}

