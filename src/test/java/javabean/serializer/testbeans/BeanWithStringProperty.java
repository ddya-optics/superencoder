package javabean.serializer.testbeans;

import java.util.Objects;

public class BeanWithStringProperty extends BeanWithWrapperProperties {
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithStringProperty that = (BeanWithStringProperty) o;
        return Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), string);
    }
}
