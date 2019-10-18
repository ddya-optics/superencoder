package javabean.serializer;

import javabean.serializer.testbeans.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class TestInitializers {
    public static void initializeBeanWithPrimitiveProperties(BeanWithPrimitiveProperties bean) {
        bean.setOneBool(true);
        bean.setOneByte((byte) 1);
        bean.setOneChar('a');
        bean.setOneDouble(0.1);
        bean.setOneFloat(0.2f);
        bean.setOneInt(3);
        bean.setOneShort((short) 5);
        bean.setOneLong(6);
    }

    public static void initializeBeanWithWrapperProperties(BeanWithWrapperProperties bean) {
        bean.setOneBoolWrapper(true);
        bean.setOneByteWrapper((byte) 2);
        bean.setOneCharWrapper('b');
        bean.setOneDoubleWrapper(0.2);
        bean.setOneFloatWrapper(0.3f);
        bean.setOneIntWrapper(4);
        bean.setOneShortWrapper((short) 7);
        bean.setOneLongWrapper((long) 8);
    }

    public static void initializeBeanWithStringProperty(BeanWithStringProperty bean) {
        bean.setString("Hello world");
    }

    public static void initializeBeanWithInstantProperty(BeanWithInstantProperty bean) {
        bean.setInstant(Instant.now());
    }

    public static void initializeBeanWithBigDecimalProperty(BeanWithBigDecimalProperty bean) {
        bean.setBigDecimal(new BigDecimal("5382367893276403276.6040397832763482347026432463"));
    }

    public static void initializeBeanWithBeanProperty(BeanWithBeanProperty bean) {
        BeanWithPrimitiveProperties beanProperty = new BeanWithPrimitiveProperties();
        initializeBeanWithPrimitiveProperties(beanProperty);
        bean.setBean(beanProperty);
        bean.setBigDecimal(new BigDecimal("5382367893276403276.6040397832763482347026432463"));
    }

    public static void initializeBeanWithListProperty(BeanWithListProperty bean) {
        List<BeanWithPrimitiveProperties> list = new ArrayList<>();
        BeanWithBeanProperty bean1 = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        list.add(bean1);
        list.add(null);
        list.add(bean2);
        list.add(bean2);
        bean.setList(list);
    }

    public static void initializeBeanWithSetProperty(BeanWithSetProperty bean) {
        Set<BeanWithPrimitiveProperties> set = new HashSet<>();
        BeanWithBeanProperty bean1 = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        set.add(bean1);
        set.add(null);
        set.add(bean2);
        set.add(bean2);
        bean.setSet(set);
    }

    public static void initializeBeanWithMapProperty(BeanWithMapProperty bean) {
        Map<BeanWithPrimitiveProperties,BeanWithPrimitiveProperties> map = new HashMap<>();
        BeanWithBeanProperty bean1 = new BeanWithBeanProperty();
        TestInitializers.initializeBeanWithInstantProperty(bean);
        BeanWithBigDecimalProperty bean2 = new BeanWithBigDecimalProperty();
        TestInitializers.initializeBeanWithBigDecimalProperty(bean2);
        map.put(bean1,null);
        map.put(null,bean1);
        map.put(bean2,bean1);
        bean.setMap(map);
    }

}
