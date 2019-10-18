package javabean.serializer.testbeans;

import java.util.Objects;

public class BeanWithBeanProperty extends BeanWithBigDecimalProperty {
    private BeanWithPrimitiveProperties bean;

    public BeanWithPrimitiveProperties getBean() {
        return bean;
    }

    public void setBean(BeanWithPrimitiveProperties bean) {
        this.bean = bean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithBeanProperty that = (BeanWithBeanProperty) o;
        return Objects.equals(bean, that.bean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bean);
    }
}
