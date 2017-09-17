package com.xhr.mySnowflakeOid.other;

import java.io.Serializable;
import java.util.List;

/**
 * @author 徐浩然
 * @version Serializer, 2017-09-17
 */
public interface Serializer
{
    byte[] marshalToByte(Serializable var1);

    String marshalToString(Serializable var1);

    Serializable unMarshal(byte[] var1);

    Serializable unMarshal(String var1);

    List<Serializable> unMarshal(List<byte[]> var1);
}
