package com.xhr.mySnowflakeOid.other;


import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.nosql.redis.JedisTemplate;
import org.springside.modules.nosql.redis.JedisUtils;
import org.springside.modules.nosql.redis.JedisTemplate.JedisAction;
import org.springside.modules.nosql.redis.JedisTemplate.JedisActionNoResult;
import org.springside.modules.nosql.redis.JedisTemplate.PipelineAction;
import org.springside.modules.nosql.redis.JedisTemplate.PipelineActionNoResult;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author 徐浩然
 * @version CacheManager, 2017-09-17
*/

public class CacheManager implements ICacheManager {
    public static final String DEFAULT_CHARSET = "UTF-8";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Serializer serializer = new DefaultJDKSerializer();
    private JedisTemplate jedisTemplate;

    public CacheManager() {
    }

    public JedisTemplate getJedisTemplate() {
        return this.jedisTemplate;
    }

    public void setJedisTemplate(JedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }

    public <T extends Serializable> void set(final String key, final T value) {
        this.execute(new JedisActionNoResult() {
            public void action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                byte[] valueBytes = CacheManager.this.serializer.marshalToByte(value);
                jedis.set(keyBytes, valueBytes);
            }
        });
    }

    public <T extends Serializable> void setex(final String key, final T value, final int timeout) {
        this.execute(new JedisActionNoResult() {
            public void action(Jedis jedis) {
                byte[] valueBytes = CacheManager.this.serializer.marshalToByte(value);
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                jedis.setex(keyBytes, timeout, valueBytes);
            }
        });
    }

    public void expire(final String key, final int timeout) {
        this.execute(new JedisActionNoResult() {
            public void action(Jedis jedis) {
                jedis.expire(key, timeout);
            }
        });
    }

    public <T extends Serializable> void setAndExpireInPipeline(final String key, final T value, final int timeout) {
        this.jedisTemplate.execute(new PipelineActionNoResult() {
            public void action(Pipeline pipeline) {
                byte[] valueBytes = CacheManager.this.serializer.marshalToByte(value);
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                pipeline.set(keyBytes, valueBytes);
                pipeline.expire(key, timeout);
            }
        });
    }

    public void piplineExecute(PipelineActionNoResult action) {
        this.execute(action);
    }

    public <T> List<T> piplineExecute(PipelineAction action) {
        return (List<T>) this.execute(action);
    }

    public Boolean exists(final String key) {
        return (Boolean)this.execute(new JedisAction() {
            public Boolean action(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    public <T extends Serializable> Serializable get(final String key) {
        return (Serializable)this.execute(new JedisAction() {
            public T action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                if(keyBytes == null) {
                    return null;
                } else {
                    byte[] valueBytes = jedis.get(keyBytes);
                    return valueBytes == null?null: (T) CacheManager.this.serializer.unMarshal(valueBytes);
                }
            }
        });
    }

    public <T extends Serializable> T hget(final String key, final String fieldName) {
        return (T) this.execute(new JedisAction() {
            public T action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                byte[] fieldBytes = fieldName.getBytes(Charset.forName("UTF-8"));
                byte[] attrBytes = jedis.hget(keyBytes, fieldBytes);
                return attrBytes == null?null: (T) CacheManager.this.serializer.unMarshal(attrBytes);
            }
        });
    }

    public <T extends Serializable> List<T> hmget(final String key, final String... fieldName) {
        return (List)this.execute(new JedisAction() {
            public List<T> action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                byte[][] fieldBytes = new byte[fieldName.length][];

                for(int resultList = 0; resultList < fieldName.length; ++resultList) {
                    fieldBytes[resultList] = fieldName[resultList].getBytes(Charset.forName("UTF-8"));
                }

                List var5 = jedis.hmget(keyBytes, fieldBytes);
                return var5 != null && !var5.isEmpty()?CacheManager.this.serializer.unMarshal(var5):null;
            }
        });
    }

    public Boolean hexists(final String key, final String field) {
        return (Boolean)this.execute(new JedisAction() {
            public Boolean action(Jedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    public Map<byte[], byte[]> hgetAll(final String key) {
        return (Map)this.execute(new JedisAction() {
            public Map<byte[], byte[]> action(Jedis jedis) {
                return jedis.hgetAll(key.getBytes(Charset.forName("UTF-8")));
            }
        });
    }

    public <T extends Serializable> void hset(final String key, final String fieldName, final T value) {
        this.execute(new JedisActionNoResult() {
            public void action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                byte[] fieldBytes = fieldName.getBytes(Charset.forName("UTF-8"));
                byte[] valueBytes = CacheManager.this.serializer.marshalToByte(value);
                jedis.hset(keyBytes, fieldBytes, valueBytes);
            }
        });
    }

    public <T extends Serializable> void hmset(final String key, final Map<String, T> valueMap) {
        this.execute(new JedisActionNoResult() {
            public void action(Jedis jedis) {
                byte[] keyBytes = key.getBytes(Charset.forName("UTF-8"));
                HashMap byteDataMap = new HashMap();
                Iterator var4 = valueMap.entrySet().iterator();

                while(var4.hasNext()) {
                    Entry entry = (Entry)var4.next();
                    byteDataMap.put(((String)entry.getKey()).getBytes(Charset.forName("UTF-8")), CacheManager.this.serializer.marshalToByte((Serializable)entry.getValue()));
                }

                jedis.hmset(keyBytes, byteDataMap);
            }
        });
    }

    public void removeCache(String key) {
        if(StringUtils.isNotBlank(key)) {
            this.jedisTemplate.del(new String[]{key});
        }

    }

    public void hdel(String key, String field) {
        if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(field)) {
            this.jedisTemplate.hdel(key, new String[]{field});
        }

    }

    public void hdel(String key, String... field) {
        if(field != null && field.length > 0) {
            this.jedisTemplate.hdel(key, field);
        }

    }

    public void initNumForIncr(String key, long initValue) {
        this.jedisTemplate.set(key, String.valueOf(initValue));
    }

    public Long incr(String key) {
        return this.jedisTemplate.incr(key);
    }

    public Long decr(String key) {
        return this.jedisTemplate.decr(key);
    }

    public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;

        Object e;
        try {
            jedis = (Jedis)this.jedisTemplate.getJedisPool().getResource();
            e = jedisAction.action(jedis);
        } catch (JedisException var8) {
            broken = this.handleJedisException(var8);
            throw var8;
        } finally {
            this.closeResource(jedis, broken);
        }

        return (T) e;
    }

    public void execute(JedisActionNoResult jedisAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;

        try {
            jedis = (Jedis)this.jedisTemplate.getJedisPool().getResource();
            jedisAction.action(jedis);
        } catch (JedisException var8) {
            broken = this.handleJedisException(var8);
            throw var8;
        } finally {
            this.closeResource(jedis, broken);
        }

    }

    public void execute(PipelineActionNoResult pipelineAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;

        try {
            jedis = (Jedis)this.jedisTemplate.getJedisPool().getResource();
            Pipeline e = jedis.pipelined();
            pipelineAction.action(e);
            e.sync();
        } catch (JedisException var8) {
            broken = this.handleJedisException(var8);
            throw var8;
        } finally {
            this.closeResource(jedis, broken);
        }

    }

    public List<Object> execute(PipelineAction pipelineAction) throws JedisException {
        Jedis jedis = null;
        boolean broken = false;

        List var5;
        try {
            jedis = (Jedis)this.jedisTemplate.getJedisPool().getResource();
            Pipeline e = jedis.pipelined();
            pipelineAction.action(e);
            var5 = e.syncAndReturnAll();
        } catch (JedisException var9) {
            broken = this.handleJedisException(var9);
            throw var9;
        } finally {
            this.closeResource(jedis, broken);
        }

        return var5;
    }

    protected boolean handleJedisException(JedisException jedisException) {
        if(jedisException instanceof JedisConnectionException) {
            this.logger.error("Redis connection " + this.jedisTemplate.getJedisPool().getAddress() + " lost.", jedisException);
        } else if(jedisException instanceof JedisDataException) {
            if(jedisException.getMessage() == null || jedisException.getMessage().indexOf("READONLY") == -1) {
                return false;
            }

            this.logger.error("Redis connection " + this.jedisTemplate.getJedisPool().getAddress() + " are read-only slave.", jedisException);
        } else {
            this.logger.error("Jedis exception happen.", jedisException);
        }

        return true;
    }

    protected void closeResource(Jedis jedis, boolean conectionBroken) {
        try {
            if(conectionBroken) {
                this.jedisTemplate.getJedisPool().returnBrokenResource(jedis);
            } else {
                this.jedisTemplate.getJedisPool().returnResource(jedis);
            }
        } catch (Exception var4) {
            this.logger.error("return back jedis failed, will fore close the jedis.", var4);
            JedisUtils.destroyJedis(jedis);
        }

    }

    public Serializer getSerializer() {
        return this.serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}