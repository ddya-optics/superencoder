package javabean.serializer.testbeans;

import java.util.Objects;

public class ObjectWithoutSetter {
    private int a;
    private Integer b;

    public ObjectWithoutSetter() {
    }

    public ObjectWithoutSetter(Integer b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectWithoutSetter that = (ObjectWithoutSetter) o;
        return a == that.a &&
                Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
