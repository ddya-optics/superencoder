package javabean.serializer;

import javabean.serializer.testbeans.*;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

public class BeanSerializerTest {
    @Test
    public void testGetClassName() {
        SimpleBean bean = new SimpleBean();
        BeanSerializer serializer = new BeanSerializer();
        String name = serializer.getClassName(bean.getClass());
        assertEquals("javabean.serializer.testbeans.SimpleBean",name);
    }

    @Test
    public void testCheckEmptyConstructor() {
        SimpleBean bean = new SimpleBean();
        BeanSerializer serializer = new BeanSerializer();
        try {
            serializer.checkEmptyConstructor(bean.getClass());
        } catch (BeanSerializerException ex) {
            fail();
        }
    }

    @Test
    public void testCheckEmptyConstructor2() {
        SimpleBeanWithEmptyConstructor bean = new SimpleBeanWithEmptyConstructor();
        BeanSerializer serializer = new BeanSerializer();
        try {
            serializer.checkEmptyConstructor(bean.getClass());
        } catch (BeanSerializerException ex) {
            fail();
        }
    }

    @Test(expected = BeanSerializerException.class)
    public void testCheckEmptyConstructor3() {
        BeanSerializer serializer = new BeanSerializer();
        serializer.checkEmptyConstructor(ObjectWithoutEmptyConstructor.class);
    }

    @Test
    public void testGetGetters() {
        BeanSerializer serializer = new BeanSerializer();
        List<PropertyDescriptor> list = serializer.getSerializableProperties(SimpleBean.class);
        assertEquals(3,list.size());
        list.sort((x,y)->
        {return x.getReadMethod().getName().compareTo(y.getReadMethod().getName());});
        assertEquals("getId",list.get(0).getReadMethod().getName());
        assertEquals("getName",list.get(1).getReadMethod().getName());
        assertEquals("isValid",list.get(2).getReadMethod().getName());
        assertEquals(true,list.get(0).getPropertyType().isPrimitive());
        assertEquals(false,list.get(1).getPropertyType().isPrimitive());
        assertEquals(true,list.get(2).getPropertyType().isPrimitive());
        assertEquals("int",list.get(0).getPropertyType().getName());
        assertEquals("java.lang.String",list.get(1).getPropertyType().getName());
        assertEquals("boolean",list.get(2).getPropertyType().getName());
    }

    @Test
    public void testSerializeDeserializeString() throws IOException {
        String string = "thisString";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanSerializer serializer = new BeanSerializer();
        serializer.serializeString(string,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        String deserializedString = serializer.deserializeString(inputStream);
        assertEquals(string,deserializedString);
    }

    @Test
    public void testSerializeDeserializeClassName() throws IOException, ClassNotFoundException {
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeClassName(SimpleBean.class,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        Class clazz = serializer.deserializeClassName(inputStream);
        assertEquals(SimpleBean.class,clazz);
    }

    @Test
    public void testSerializeDeserializePrimitiveProperty() throws IOException {
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanWithPrimitiveProperties bean = new BeanWithPrimitiveProperties();
        PropertyDescriptor descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneBool");
        serializer.serializePrimitiveProperty(boolean.class,true,stream);
        BeanWithPrimitiveProperties bean2 = new BeanWithPrimitiveProperties();
        ByteArrayInputStream inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,boolean.class,descriptor,inputStream);
        assertTrue(bean2.isOneBool());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneByte");
        serializer.serializePrimitiveProperty(byte.class,(byte)1,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,byte.class,descriptor,inputStream);
        assertEquals(1,bean2.getOneByte());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneChar");
        serializer.serializePrimitiveProperty(char.class,'a',stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,char.class,descriptor,inputStream);
        assertEquals('a',bean2.getOneChar());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneDouble");
        serializer.serializePrimitiveProperty(double.class,0.1,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,double.class,descriptor,inputStream);
        assertEquals(0.1,bean2.getOneDouble(),Double.MIN_VALUE);
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneFloat");
        serializer.serializePrimitiveProperty(float.class,0.2f,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,float.class,descriptor,inputStream);
        assertEquals(0.2f,bean2.getOneFloat(),Float.MIN_VALUE);
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneInt");
        serializer.serializePrimitiveProperty(int.class,3,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,int.class,descriptor,inputStream);
        assertEquals(3,bean2.getOneInt());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneShort");
        serializer.serializePrimitiveProperty(short.class,(short)5,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,short.class,descriptor,inputStream);
        assertEquals(5,bean2.getOneShort());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneLong");
        serializer.serializePrimitiveProperty(long.class,(long)6,stream);
        inputStream = getInputStream(stream);
        serializer.deserializePrimitiveProperty(bean2,long.class,descriptor,inputStream);
        assertEquals(6,bean2.getOneLong());
    }

    @Test
    public void testSerializeDeserializeWrapperProperty() throws IOException {
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanWithWrapperProperties bean = new BeanWithWrapperProperties();
        PropertyDescriptor descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneBoolWrapper");
        serializer.serializeWrapperProperty(descriptor,true,stream);
        BeanWithWrapperProperties bean2 = new BeanWithWrapperProperties();
        ByteArrayInputStream inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Boolean.class,descriptor,inputStream);
        assertTrue(bean2.getOneBoolWrapper());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneByteWrapper");
        serializer.serializeWrapperProperty(descriptor,(byte)1,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Byte.class,descriptor,inputStream);
        assertEquals(1,(byte) bean2.getOneByteWrapper());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneCharWrapper");
        serializer.serializeWrapperProperty(descriptor,'a',stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Character.class,descriptor,inputStream);
        assertEquals('a',(char)bean2.getOneCharWrapper());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneDoubleWrapper");
        serializer.serializeWrapperProperty(descriptor,0.1,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Double.class,descriptor,inputStream);
        assertEquals(0.1,(double)bean2.getOneDoubleWrapper(),Double.MIN_VALUE);
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneFloatWrapper");
        serializer.serializeWrapperProperty(descriptor,0.2f,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Float.class,descriptor,inputStream);
        assertEquals(0.2f,(float)bean2.getOneFloatWrapper(),Float.MIN_VALUE);
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneIntWrapper");
        serializer.serializeWrapperProperty(descriptor,3,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Integer.class,descriptor,inputStream);
        assertEquals(3,(int)bean2.getOneIntWrapper());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneShortWrapper");
        serializer.serializeWrapperProperty(descriptor,(short)5,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Short.class,descriptor,inputStream);
        assertEquals(5,(short)bean2.getOneShortWrapper());
        stream = new ByteArrayOutputStream();
        descriptor = serializer.getSetterPropertyByName(bean.getClass(),"oneLongWrapper");
        serializer.serializeWrapperProperty(descriptor,(long)6,stream);
        inputStream = getInputStream(stream);
        serializer.deserializeWrapperProperty(bean2,Long.class,descriptor,inputStream);
        assertEquals(6,(long)bean2.getOneLongWrapper());
    }

    @Test
    public void testDeserializeStringProperty() throws IOException {
        String value = "Hello world!";
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeString(value,stream);
        BeanWithStringProperty bean = new BeanWithStringProperty();
        ByteArrayInputStream inputStream = getInputStream(stream);
        PropertyDescriptor descriptor = serializer.getSetterPropertyByName(bean.getClass(),"string");
        serializer.deserializeStringProperty(bean,descriptor,inputStream);
        assertEquals(value,bean.getString());
    }

    @Test
    public void testDeserializeInstantProperty() throws IOException {
        Instant instant = Instant.now();
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeInstant(instant,stream);
        BeanWithInstantProperty bean = new BeanWithInstantProperty();
        ByteArrayInputStream inputStream = getInputStream(stream);
        PropertyDescriptor descriptor = serializer.getSetterPropertyByName(bean.getClass(),"instant");
        serializer.deserializeInstantProperty(bean,descriptor,inputStream);
        assertEquals(instant,bean.getInstant());
    }

    @Test
    public void testDeserializeBigDecimalProperty() throws IOException {
        BigDecimal bigDecimal = new BigDecimal("46373274.5936568568496596704460780764374734475855595658633");
        BeanSerializer serializer = new BeanSerializer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBigDecimal(bigDecimal,stream);
        BeanWithBigDecimalProperty bean = new BeanWithBigDecimalProperty();
        ByteArrayInputStream inputStream = getInputStream(stream);
        PropertyDescriptor descriptor = serializer.getSetterPropertyByName(bean.getClass(),"bigDecimal");
        serializer.deserializeBigDecimalProperty(bean,descriptor,inputStream);
        assertEquals(bigDecimal,bean.getBigDecimal());
    }

    @Test
    public void testDeserializeBeanWithoutBeanProperties() throws IOException,ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithBigDecimalProperty bean = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithPrimitiveProperties(bean);
        TestInitializers.initializeBeanWithWrapperProperties(bean);
        TestInitializers.initializeBeanWithStringProperty(bean);
        TestInitializers.initializeBeanWithInstantProperty(bean);
        TestInitializers.initializeBeanWithBigDecimalProperty(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        BeanWithBigDecimalProperty deserializedBean =
                (BeanWithBigDecimalProperty) serializer.deserializeBean(inputStream);
        assertEquals(bean,deserializedBean);

    }

    @Test
    public void testDeserializeBeanWithBeanProperty() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithBeanProperty bean = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithPrimitiveProperties(bean);
        TestInitializers.initializeBeanWithWrapperProperties(bean);
        TestInitializers.initializeBeanWithStringProperty(bean);
        TestInitializers.initializeBeanWithInstantProperty(bean);
        TestInitializers.initializeBeanWithBigDecimalProperty(bean);
        TestInitializers.initializeBeanWithBeanProperty(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        BeanWithBeanProperty deserializedBean =
                (BeanWithBeanProperty) serializer.deserializeBean(inputStream);
        assertEquals(bean,deserializedBean);
    }

    @Test
    public void testDeserializeList()throws IOException, ClassNotFoundException {
        List<BeanWithPrimitiveProperties> list = new ArrayList<>();
        BeanWithBeanProperty bean = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        list.add(bean);
        list.add(null);
        list.add(bean2);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanSerializer serializer = new BeanSerializer();
        serializer.serializeList(list,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        List<BeanWithPrimitiveProperties> deserializedList  =
                serializer.deserializeList(inputStream);
        assertEquals(list,deserializedList);
    }

    @Test
    public void testDeserializeBeanWithListProperty() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithListProperty bean = new BeanWithListProperty();
        TestInitializers.initializeBeanWithPrimitiveProperties(bean);
        TestInitializers.initializeBeanWithWrapperProperties(bean);
        TestInitializers.initializeBeanWithStringProperty(bean);
        TestInitializers.initializeBeanWithInstantProperty(bean);
        TestInitializers.initializeBeanWithBigDecimalProperty(bean);
        TestInitializers.initializeBeanWithBeanProperty(bean);
        TestInitializers.initializeBeanWithListProperty(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        BeanWithListProperty deserializedBean =
                (BeanWithListProperty) serializer.deserializeBean(inputStream);
        assertEquals(bean,deserializedBean);
    }

    @Test(expected = BeanSerializerException.class)
    public void testDeserializeBeanWithListPropertyAndCyclicReference() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithListProperty bean = new BeanWithListProperty();
        TestInitializers.initializeBeanWithListProperty(bean);
        bean.getList().add(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        fail();
    }

    @Test
    public void testDeserializeSet() throws IOException, ClassNotFoundException {
        Set<BeanWithPrimitiveProperties> set = new HashSet<>();
        BeanWithBeanProperty bean = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        set.add(bean);
        set.add(null);
        set.add(bean2);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanSerializer serializer = new BeanSerializer();
        serializer.serializeSet(set,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        Set<BeanWithPrimitiveProperties> deserializedSet  =
                serializer.deserializeSet(inputStream);
        assertEquals(set,deserializedSet);
    }

    @Test
    public void testDeserializeBeanWithSetProperty() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithSetProperty bean = new BeanWithSetProperty();
        TestInitializers.initializeBeanWithPrimitiveProperties(bean);
        TestInitializers.initializeBeanWithWrapperProperties(bean);
        TestInitializers.initializeBeanWithStringProperty(bean);
        TestInitializers.initializeBeanWithInstantProperty(bean);
        TestInitializers.initializeBeanWithBigDecimalProperty(bean);
        TestInitializers.initializeBeanWithBeanProperty(bean);
        TestInitializers.initializeBeanWithListProperty(bean);
        TestInitializers.initializeBeanWithSetProperty(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        BeanWithSetProperty deserializedBean =
                (BeanWithSetProperty) serializer.deserializeBean(inputStream);
        assertEquals(bean,deserializedBean);
    }

    @Test(expected = BeanSerializerException.class)
    public void testDeserializeBeanWithSetPropertyAndCyclicReference() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithSetProperty bean = new BeanWithSetProperty();
        TestInitializers.initializeBeanWithSetProperty(bean);
        bean.getSet().add(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        fail();
    }

    @Test
    public void testSerializeMap() throws IOException, ClassNotFoundException {
        Map<BeanWithPrimitiveProperties,BeanWithPrimitiveProperties> map = new HashMap<>();
        BeanWithBeanProperty bean = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        map.put(bean,null);
        map.put(null,bean);
        map.put(bean2,bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BeanSerializer serializer = new BeanSerializer();
        serializer.serializeMap(map,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        Map<BeanWithPrimitiveProperties,BeanWithPrimitiveProperties> deserializedMap  =
                serializer.deserializeMap(inputStream);
        assertEquals(map,deserializedMap);
    }

    @Test
    public void testDeserializeBeanWithMapProperty() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithMapProperty bean = new BeanWithMapProperty();
        TestInitializers.initializeBeanWithPrimitiveProperties(bean);
        TestInitializers.initializeBeanWithWrapperProperties(bean);
        TestInitializers.initializeBeanWithStringProperty(bean);
        TestInitializers.initializeBeanWithInstantProperty(bean);
        TestInitializers.initializeBeanWithBigDecimalProperty(bean);
        TestInitializers.initializeBeanWithBeanProperty(bean);
        TestInitializers.initializeBeanWithListProperty(bean);
        TestInitializers.initializeBeanWithSetProperty(bean);
        TestInitializers.initializeBeanWithMapProperty(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        ByteArrayInputStream inputStream = getInputStream(stream);
        BeanWithMapProperty deserializedBean =
                (BeanWithMapProperty) serializer.deserializeBean(inputStream);
        assertEquals(bean,deserializedBean);
    }

    @Test(expected = BeanSerializerException.class)
    public void testDeserializeBeanWithMapPropertyAndCyclicReference() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithMapProperty bean = new BeanWithMapProperty();
        TestInitializers.initializeBeanWithMapProperty(bean);
        bean.getMap().put(bean,null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        fail();
    }


    @Test(expected = BeanSerializerException.class)
    public void testSerializeBeanWithCyclicReference() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithCyclicReference bean = new BeanWithCyclicReference();
        bean.setBean(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        fail();
    }

    @Test(expected = BeanSerializerException.class)
    public void testSerializeBeanWithCyclicReference2() throws IOException, ClassNotFoundException{
        BeanSerializer serializer = new BeanSerializer();
        BeanWithCyclicReference bean = new BeanWithCyclicReference();
        BeanWithCyclicReference bean2 = new BeanWithCyclicReference();
        BeanWithCyclicReference bean3 = new BeanWithCyclicReference();
        bean.setBean(bean2);
        bean2.setBean(bean3);
        bean3.setBean(bean);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serializeBean(bean,stream);
        fail();
    }


    private ByteArrayInputStream getInputStream(ByteArrayOutputStream stream) {
        byte[] bytes = stream.toByteArray();
        return new ByteArrayInputStream(bytes);
    }

}
