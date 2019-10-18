package javabean.serializer.testbeans;

import java.math.BigDecimal;
import java.util.Objects;

public class BeanWithBigDecimalProperty extends BeanWithInstantProperty {
    private BigDecimal bigDecimal;

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithBigDecimalProperty that = (BeanWithBigDecimalProperty) o;
        return Objects.equals(bigDecimal, that.bigDecimal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bigDecimal);
    }
}
