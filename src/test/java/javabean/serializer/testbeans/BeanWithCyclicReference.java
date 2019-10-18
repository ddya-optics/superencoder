package javabean.serializer.testbeans;

public class BeanWithCyclicReference {
    private BeanWithCyclicReference bean;

    public BeanWithCyclicReference getBean() {
        return bean;
    }

    public void setBean(BeanWithCyclicReference bean) {
        this.bean = bean;
    }
}
