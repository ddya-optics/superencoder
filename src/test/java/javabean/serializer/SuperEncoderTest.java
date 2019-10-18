package javabean.serializer;
import javabean.serializer.testbeans.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SuperEncoderTest {

    @Test
    public void testDecodeEncodeComplexObject() {
        SuperEncoder encoder = new BeanSerializer();
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
        byte[] encodedObject = encoder.serialize(bean);
        BeanWithMapProperty decodedBean = (BeanWithMapProperty) encoder.deserialize(encodedObject);
        assertEquals(bean,decodedBean);
    }

    @Test(expected = BeanSerializerException.class)
    public void testDecodeEncodeComplexObjectWithComplexCyclicReference() {
        SuperEncoder encoder = new BeanSerializer();
        BeanWithMapProperty bean = new BeanWithMapProperty();
        BeanWithBeanProperty bean2 = new BeanWithBeanProperty();
        BeanWithBeanProperty bean3 = new BeanWithBeanProperty();
        bean.setList(new ArrayList<>());
        bean.getList().add(new BeanWithPrimitiveProperties());
        bean.getList().add(new BeanWithPrimitiveProperties());
        bean.getList().add(bean2);
        bean.getList().add(new BeanWithPrimitiveProperties());
        bean2.setBean(bean3);
        bean3.setBean(bean);
        encoder.serialize(bean);
        fail();
    }

    @Test
    public void testSerializeClassWithoutGetter() {
        ObjectWithoutGetter object = new ObjectWithoutGetter();
        object.setA(3);
        object.setB(5);
        SuperEncoder encoder = new BeanSerializer();
        byte[] encodedObject = encoder.serialize(object);
        ObjectWithoutGetter decodedObject = (ObjectWithoutGetter) encoder.deserialize(encodedObject);
        assertNotEquals(object,decodedObject);
    }

    @Test
    public void testSerializeClassWithoutSetter() {
        ObjectWithoutSetter objectWithoutSetter = new ObjectWithoutSetter(3);
        objectWithoutSetter.setA(2);
        SuperEncoder encoder = new BeanSerializer();
        byte[] encodedObject = encoder.serialize(objectWithoutSetter);
        ObjectWithoutSetter decodedObject = (ObjectWithoutSetter) encoder.deserialize(encodedObject);
        assertNotEquals(objectWithoutSetter,decodedObject);
        assertNull(decodedObject.getB());
    }

    @Test(expected = BeanSerializerException.class)
    public void testSerializeNull() {
        SuperEncoder encoder = new BeanSerializer();
        encoder.serialize(null);
    }

    @Test(expected = BeanSerializerException.class)
    public void testDeserializeNull() {
        SuperEncoder encoder = new BeanSerializer();
        encoder.deserialize(null);
    }
}
