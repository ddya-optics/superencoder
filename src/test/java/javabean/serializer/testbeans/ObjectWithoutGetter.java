package javabean.serializer.testbeans;

import java.util.Objects;

public class ObjectWithoutGetter {
    private int a;
    private Integer b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectWithoutGetter that = (ObjectWithoutGetter) o;
        return a == that.a &&
                Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
