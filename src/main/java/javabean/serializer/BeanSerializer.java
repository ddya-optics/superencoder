package javabean.serializer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;

public class BeanSerializer implements SuperEncoder {

    private Map<Class,Class> wrapperClasses;
    private CycledReferenceAnalyzer currentParent;

    public BeanSerializer() {
        this.wrapperClasses = new HashMap<>();
        this.wrapperClasses.put(Boolean.class,boolean.class);
        this.wrapperClasses.put(Byte.class,byte.class);
        this.wrapperClasses.put(Character.class,char.class);
        this.wrapperClasses.put(Double.class,double.class);
        this.wrapperClasses.put(Float.class,float.class);
        this.wrapperClasses.put(Integer.class,int.class);
        this.wrapperClasses.put(Long.class,long.class);
        this.wrapperClasses.put(Short.class,short.class);
    }

    public byte[] serialize(Object bean) {
        if (bean==null) {
            throw new BeanSerializerException("Bean is null");
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            serializeBean(bean,outputStream);
            currentParent=null;
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BeanSerializerException("Serialization failed", ex);
        }
    }

    public Object deserialize(byte[] data) {
        if (data==null) {
            throw new BeanSerializerException("Byte array is null");
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            return deserializeBean(inputStream);
        } catch (IOException|ClassNotFoundException e) {
            throw new BeanSerializerException("Deserialization failed", e);
        }
    }

    public void serializeBean(Object bean, ByteArrayOutputStream stream) throws IOException {
        Class clazz = bean.getClass();
        checkEmptyConstructor(clazz);
        serializeClassName(clazz,stream);
        serializeBeanBody(clazz,bean,stream);
    }

    public void serializeBeanBody(Class clazz, Object bean, ByteArrayOutputStream stream) throws IOException {
        CycledReferenceAnalyzer analyzer = new CycledReferenceAnalyzer(currentParent,bean);
        CycledReferenceAnalyzer previousParent = currentParent;
        currentParent = analyzer;
        if (analyzer.hasCyclicReferences()) {
            throw new BeanSerializerException("Serialization failed due to cyclic reference");
        }

        List<PropertyDescriptor> properties = getSerializableProperties(clazz);
        for (PropertyDescriptor property: properties
        ) {
            Class propertyType = property.getPropertyType();
            Object value = getPropertyValue(bean,property);
            if (value!=null) {
                serializeString(property.getName(), stream);
                if (propertyType.isPrimitive()) {
                    serializePrimitiveProperty(propertyType, value, stream);
                } else if (isWrapper(propertyType)) {
                    serializeWrapperProperty(property,value,stream);
                } else if (propertyType.equals(String.class)) {
                    serializeString((String) value,stream);
                } else if (propertyType.equals(Instant.class)) {
                    serializeInstant((Instant) value, stream);
                } else if (propertyType.equals(BigDecimal.class)) {
                    serializeBigDecimal((BigDecimal) value,stream);
                } else if (propertyType.equals(List.class)) {
                    serializeList((List)value,stream);
                } else if (propertyType.equals(Set.class)) {
                    serializeSet((Set)value,stream);
                } else if (propertyType.equals(Map.class)) {
                    serializeMap((Map)value,stream);
                } else  {
                    serializeBean(value,stream);
                }
            }
        }
        serializeString("",stream);
        currentParent = previousParent;

    }

    public Object deserializeBean(ByteArrayInputStream stream) throws IOException,ClassNotFoundException{
        Class clazz = deserializeClassName(stream);
        return deserializeBeanBody(clazz,stream);
    }

    public Object deserializeBeanBody(Class clazz, ByteArrayInputStream stream) throws IOException,ClassNotFoundException {
        try {
            Object object = clazz.newInstance();
            String propertyName=deserializeString(stream);
            while (!"".equals(propertyName)) {
                PropertyDescriptor property = getSetterPropertyByName(clazz,propertyName);
                if (property==null) {
                    throw new BeanSerializerException("Property with name " + property.getName() +
                            " doesn't exist or doesn't have a setter");
                }
                Class propertyType = property.getPropertyType();
                if (propertyType.isPrimitive()) {
                    deserializePrimitiveProperty(object,propertyType,property,stream);
                } else if (isWrapper(propertyType)) {
                    deserializeWrapperProperty(object,propertyType,property,stream);
                } else if (propertyType.equals(String.class)) {
                    deserializeStringProperty(object,property,stream);
                } else if (propertyType.equals(Instant.class)) {
                    deserializeInstantProperty(object,property,stream);
                } else if (propertyType.equals(BigDecimal.class)) {
                    deserializeBigDecimalProperty(object,property,stream);
                }else if (propertyType.equals(List.class)) {
                    List value = deserializeList(stream);
                    invokeObject(object,property,value);
                } else if (propertyType.equals(Set.class)) {
                    Set value = deserializeSet(stream);
                    invokeObject(object,property,value);
                } else if (propertyType.equals(Map.class)) {
                    Map value = deserializeMap(stream);
                    invokeObject(object,property,value);
                } else {
                    Object value = deserializeBean(stream);
                    invokeObject(object,property,value);
                }
                propertyName=deserializeString(stream);
            }
            return object;
        } catch (InstantiationException|IllegalAccessException e) {
            throw new BeanSerializerException("Failed to create an instance of an object of class " +clazz.getName(),e);
        }
    }

    public void serializeClassName(Class clazz, ByteArrayOutputStream stream) throws IOException {
        String className = clazz.getName();
        serializeString(className,stream);
    }

    public Class deserializeClassName(ByteArrayInputStream stream) throws IOException, ClassNotFoundException {
        String className = deserializeString(stream);
        return Class.forName(className);
    }

    public void putLengthValue(int length, ByteArrayOutputStream stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(length);
        stream.write(buffer.array());
    }

    public void serializeString(String string, ByteArrayOutputStream stream) throws IOException {
        byte[] stringEncoded = string.getBytes();
        int stringEncodedSize = stringEncoded.length;
        putLengthValue(stringEncodedSize,stream);
        stream.write(stringEncoded);
    }

    public String deserializeString(ByteArrayInputStream stream) throws IOException{
        byte[] nameEncoded = new byte[getLengthValue(stream)];
        stream.read(nameEncoded);
        return new String(nameEncoded);
    }

    public int getLengthValue(ByteArrayInputStream stream) throws IOException {
        byte[] nameLength = new byte[4];
        stream.read(nameLength);
        ByteBuffer byteBuffer = ByteBuffer.wrap(nameLength);
        return byteBuffer.getInt();
    }

    public String getClassName(Class clazz) {
        return clazz.getName();
    }

    public void invokeObject(Object object, PropertyDescriptor property, Object value) {
        try {
            property.getWriteMethod().invoke(object, value);
        } catch (IllegalAccessException|InvocationTargetException ex) {
            throw new BeanSerializerException("Failed to set value using write method "+ property.getWriteMethod().getName(),ex);
        }
    }

    public void serializePrimitiveProperty(Class clazz,
                                           Object value,
                                           ByteArrayOutputStream stream)
    throws IOException {
        int size = getPrimitiveClassSize(clazz);
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        serializePrimitivePropertyValue(clazz,value,stream,byteBuffer);
    }

    public Object getDeserializedPrimitivePropertyValue(Class clazz,
                                                        ByteArrayInputStream stream)
    throws IOException{
        int size = getPrimitiveClassSize(clazz);
        byte[] result = new byte[size];
        stream.read(result);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        return deserializePrimitivePropertyValue(clazz, buffer);
    }

    public void deserializePrimitiveProperty(Object object, Class clazz,
                                             PropertyDescriptor property,
                                             ByteArrayInputStream stream) throws IOException{
        Object value = getDeserializedPrimitivePropertyValue(clazz, stream);
        invokeObject(object,property,value);
    }

    public void deserializeStringProperty(Object object, PropertyDescriptor property, ByteArrayInputStream stream)
    throws IOException{
        String value = deserializeString(stream);
        invokeObject(object,property,value);
    }

    public void serializeInstant(Instant value, ByteArrayOutputStream stream) throws IOException {
        long epochSeconds = value.getEpochSecond();
        serializePrimitiveProperty(long.class,epochSeconds,stream);
        int nanoseconds = value.getNano();
        serializePrimitiveProperty(int.class,nanoseconds,stream);
    }

    public void deserializeInstantProperty(Object object, PropertyDescriptor property, ByteArrayInputStream stream)
            throws IOException{
        long epochSeconds = (long) getDeserializedPrimitivePropertyValue(long.class,stream);
        int nanoseconds = (int) getDeserializedPrimitivePropertyValue(int.class,stream);
        Instant instant = Instant.ofEpochSecond(epochSeconds).plusNanos(nanoseconds);
        invokeObject(object,property,instant);
    }

    public void serializeBigDecimal(BigDecimal bigDecimal, ByteArrayOutputStream stream)
    throws IOException{
        serializeString(bigDecimal.toString(),stream);
    }

    public void deserializeBigDecimalProperty(Object object, PropertyDescriptor property, ByteArrayInputStream stream)
            throws IOException{
        BigDecimal value = new BigDecimal(deserializeString(stream));
        invokeObject(object,property,value);
    }


    public Object getPropertyValue(Object object,
                                   PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        try {
            return readMethod.invoke(object);
        } catch (IllegalAccessException|InvocationTargetException ex) {
            throw new BeanSerializerException("Failed to get value using method "+ readMethod.getName(),ex);
        }

    }

    public void serializeWrapperProperty(PropertyDescriptor property,
                                         Object value,
                                         ByteArrayOutputStream stream)
            throws IOException {
        Class primitiveClass = wrapperClasses.get(property.getPropertyType());
        serializePrimitiveProperty(primitiveClass,value,stream);
    }

    public void deserializeWrapperProperty(Object object, Class clazz,
                                           PropertyDescriptor property,
                                           ByteArrayInputStream stream) throws IOException {
        Class primitiveClass = wrapperClasses.get(clazz);
        int size = getPrimitiveClassSize(primitiveClass);
        byte[] result = new byte[size];
        stream.read(result);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        Object value = deserializePrimitivePropertyValue(primitiveClass, buffer);
        invokeObject(object,property,value);
    }

    public void serializeList(List list, ByteArrayOutputStream stream) throws IOException {
        putLengthValue(list.size(),stream);
        serializeClassName(list.getClass(),stream);
        for (int i=0; i<list.size();i++) {
            serializeCollectionBean(list.get(i),stream);
        }
    }

    public List deserializeList(ByteArrayInputStream stream) throws IOException, ClassNotFoundException {
        int length = getLengthValue(stream);
        Class clazz = deserializeClassName(stream);
        CollectionInitializer<List> initializer = new CollectionInitializer<>();
        List list = initializer.createInstanceOfCollection(clazz,length);
        for (int i=0;i<length;i++) {
            Object object = deserializeCollectionBean(stream);
            list.add(object);
        }
        return list;
    }

    public void serializeSet(Set set, ByteArrayOutputStream stream) throws IOException {
        putLengthValue(set.size(),stream);
        serializeClassName(set.getClass(),stream);
        for (Object object:
             set) {
            serializeCollectionBean(object,stream);
        }
    }

    public Set deserializeSet(ByteArrayInputStream stream) throws IOException, ClassNotFoundException {
        int length = getLengthValue(stream);
        Class clazz = deserializeClassName(stream);
        CollectionInitializer<Set> initializer = new CollectionInitializer<>();
        Set set = initializer.createInstanceOfCollection(clazz,length);
        for (int i=0;i<length;i++) {
            Object object = deserializeCollectionBean(stream);
            set.add(object);
        }
        return set;
    }

    public void serializeMap(Map map, ByteArrayOutputStream stream) throws IOException {
        putLengthValue(map.size(),stream);
        serializeClassName(map.getClass(),stream);
        for (Object object:
                map.keySet()) {
            serializeCollectionBean(object,stream);
            serializeCollectionBean(map.get(object),stream);
        }
    }

    public Map deserializeMap(ByteArrayInputStream stream) throws IOException, ClassNotFoundException {
        int length = getLengthValue(stream);
        Class clazz = deserializeClassName(stream);
        CollectionInitializer<Map> initializer = new CollectionInitializer<>();
        Map map = initializer.createInstanceOfCollection(clazz,length);
        for (int i=0;i<length;i++) {
            Object key = deserializeCollectionBean(stream);
            Object value = deserializeCollectionBean(stream);
            map.put(key,value);
        }
        return map;
    }

    public void serializeCollectionBean(Object object, ByteArrayOutputStream stream) throws IOException {
        if (object==null) {
            serializeString("",stream);
        } else {
            Class clazz = object.getClass();
            serializeClassName(clazz,stream);
            serializeBeanBody(clazz,object,stream);
        }
    }

    public Object deserializeCollectionBean(ByteArrayInputStream stream) throws IOException,ClassNotFoundException{
        String className = deserializeString(stream);
        if ("".equals(className)) {
            return null;
        } else {
            Class clazz = Class.forName(className);
            return deserializeBeanBody(clazz,stream);
        }
    }
    public void checkEmptyConstructor(Class clazz) {
        try {
            clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BeanSerializerException("Class must have an empty constructor to be serializable",e);
        }
    }

    public List<PropertyDescriptor> getSerializableProperties(Class clazz) {
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
            List<PropertyDescriptor> getters = new ArrayList<>();
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor descriptor:
                 propertyDescriptors) {
                if (descriptor.getReadMethod()!=null&&descriptor.getWriteMethod()!=null) {
                    getters.add(descriptor);
                }
            }
            return getters;
        } catch (IntrospectionException ex) {
            throw new BeanSerializerException("Getter analysis failed",ex);
        }
    }

    public PropertyDescriptor getSetterPropertyByName(Class clazz, String name) {
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor descriptor:
                    propertyDescriptors) {
                if (descriptor.getName().equals(name)&&descriptor.getWriteMethod()!=null) {
                    return descriptor;
                }
            }
            return null;
        } catch (IntrospectionException ex) {
            throw new BeanSerializerException("Setter analysis failed",ex);
        }
    }


    public int getPrimitiveClassSize(Class clazz) {
        switch (clazz.getName()) {
            case ("boolean"):
                return 4;
            case("byte"):
                return Byte.BYTES;
            case ("char"):
                return Character.BYTES;
            case ("double"):
                return Double.BYTES;
            case ("float"):
                    return Float.BYTES;
            case ("int"):
                return Integer.BYTES;
            case ("long"):
                return Long.BYTES;
            case ("short"):
                return Short.BYTES;
            default:
                throw new BeanSerializerException("This is not a primitive type: "+clazz.getName());
        }
    }

    public void serializePrimitivePropertyValue(Class clazz, Object result,
                                                ByteArrayOutputStream stream,
                                                ByteBuffer buffer)
    throws IOException {
        switch (clazz.getName()) {
            case ("boolean"):
                buffer.putInt((boolean) result ? 1 : 0);
                stream.write(buffer.array());
                break;
            case ("byte"):
                stream.write(new byte[]{(byte) result});
                break;
            case ("char"):
                buffer.putChar((char) result);
                stream.write(buffer.array());
                break;
            case ("double"):
                buffer.putDouble((double) result);
                stream.write(buffer.array());
                break;
            case ("float"):
                buffer.putFloat((float) result);
                stream.write(buffer.array());
                break;
            case ("int"):
                buffer.putInt((int) result);
                stream.write(buffer.array());
                break;
            case ("long"):
                buffer.putLong((long) result);
                stream.write(buffer.array());
                break;
            case ("short"):
                buffer.putShort((short) result);
                stream.write(buffer.array());
                break;
            default:
                throw new BeanSerializerException("Failed to serialize property of the primitive type: " + clazz.getName());
        }
    }

    public Object deserializePrimitivePropertyValue(Class clazz,
                                                ByteBuffer buffer)
            throws IOException {
        switch (clazz.getName()) {
            case ("boolean"):
                return buffer.getInt()==1;
            case ("byte"):
                return buffer.array()[0];
            case ("char"):
                return buffer.getChar();
            case ("double"):
                return buffer.getDouble();
            case ("float"):
                return buffer.getFloat();
            case ("int"):
                return buffer.getInt();
            case ("long"):
                return buffer.getLong();
            case ("short"):
                return buffer.getShort();
            default:
                throw new BeanSerializerException("Failed to serialize property of the primitive type: " + clazz.getName());
        }
    }

    public boolean isWrapper(Class clazz) {
        return wrapperClasses.containsKey(clazz);
    }

}
