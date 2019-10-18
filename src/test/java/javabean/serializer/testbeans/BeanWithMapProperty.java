package javabean.serializer.testbeans;

import java.util.Map;
import java.util.Objects;

public class BeanWithMapProperty extends BeanWithSetProperty {
    private Map<BeanWithPrimitiveProperties,BeanWithPrimitiveProperties> map;

    public Map<BeanWithPrimitiveProperties, BeanWithPrimitiveProperties> getMap() {
        return map;
    }

    public void setMap(Map<BeanWithPrimitiveProperties, BeanWithPrimitiveProperties> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithMapProperty that = (BeanWithMapProperty) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), map);
    }
}
