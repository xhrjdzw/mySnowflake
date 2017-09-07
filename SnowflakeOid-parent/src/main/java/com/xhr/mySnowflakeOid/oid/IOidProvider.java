package com.xhr.mySnowflakeOid.oid;

/**
 * 总接口
 * @author 徐浩然
 * @version IOidProvider, 2017-09-07
 */
public interface IOidProvider {

    public String generatorID(String module);
}
