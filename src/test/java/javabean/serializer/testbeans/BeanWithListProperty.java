package javabean.serializer.testbeans;

import java.util.List;
import java.util.Objects;

public class BeanWithListProperty extends BeanWithBeanProperty {
    private List<BeanWithPrimitiveProperties> list;

    public List<BeanWithPrimitiveProperties> getList() {
        return list;
    }

    public void setList(List<BeanWithPrimitiveProperties> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithListProperty that = (BeanWithListProperty) o;
        return Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), list);
    }
}
