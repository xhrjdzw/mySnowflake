package com.xhr.mySnowflakeOid.other;

import com.xhr.mySnowflakeOid.utils.SerializUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 徐浩然
 * @version DefaultJDKSerializer, 2017-09-17
 */
public class DefaultJDKSerializer implements Serializer {
    public DefaultJDKSerializer() {
    }

    public Serializable unMarshal(String data) {
        return (Serializable) SerializUtil.getObjectFromStr(data);
    }

    public String marshalToString(Serializable data) {
        return SerializUtil.getStrFromObj(data);
    }

    public Serializable unMarshal(byte[] data) {
        return (Serializable)SerializUtil.byteToObject(data);
    }

    public byte[] marshalToByte(Serializable data) {
        return SerializUtil.objectToByte(data);
    }

    public List<Serializable> unMarshal(List<byte[]> dataList) {
        ArrayList resultList = new ArrayList();
        if(dataList != null && !dataList.isEmpty()) {
            Iterator var3 = dataList.iterator();

            while(var3.hasNext()) {
                byte[] data = (byte[])var3.next();
                if(data != null) {
                    resultList.add((Serializable)SerializUtil.byteToObject(data));
                }
            }
        }

        return resultList;
    }
}
