package javabean.serializer.testbeans;

import java.util.Objects;
import java.util.Set;

public class BeanWithSetProperty extends BeanWithListProperty {
    private Set<BeanWithPrimitiveProperties> set;

    public Set<BeanWithPrimitiveProperties> getSet() {
        return set;
    }

    public void setSet(Set<BeanWithPrimitiveProperties> set) {
        this.set = set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithSetProperty that = (BeanWithSetProperty) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), set);
    }
}
