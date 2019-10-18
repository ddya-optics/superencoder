package javabean.serializer;

public interface SuperEncoder {
    byte[] serialize(Object bean);
    Object deserialize(byte[] data);
}
