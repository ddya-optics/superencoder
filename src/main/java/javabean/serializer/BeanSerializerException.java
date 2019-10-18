package javabean.serializer;

public class BeanSerializerException extends RuntimeException{
    public BeanSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanSerializerException(String message) {
        super(message);
    }
}
