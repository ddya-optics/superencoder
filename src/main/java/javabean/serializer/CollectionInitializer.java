package javabean.serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CollectionInitializer<T> {

    public T createInstanceOfCollection(Class clazz, int capacity) {
        try {
            try {
                Constructor constructor = clazz.getConstructor(int.class);
                return (T) constructor.newInstance(capacity);
            } catch (NoSuchMethodException| InvocationTargetException e) {
                return (T) clazz.newInstance();
            }
        } catch (InstantiationException|IllegalAccessException e) {
            throw new BeanSerializerException("Cannot create instance of a collection of type " + clazz,e);
        }
    }
}
