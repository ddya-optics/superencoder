package javabean.serializer.testbeans;

import java.time.Instant;
import java.util.Objects;

public class BeanWithInstantProperty extends BeanWithStringProperty {
    private Instant instant;

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithInstantProperty that = (BeanWithInstantProperty) o;
        return Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instant);
    }
}
