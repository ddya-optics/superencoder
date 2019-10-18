package javabean.serializer.testbeans;

import java.util.Objects;

public class BeanWithWrapperProperties extends BeanWithPrimitiveProperties {
    private Boolean oneBoolWrapper;
    private Byte oneByteWrapper;
    private Character oneCharWrapper;
    private Double oneDoubleWrapper;
    private Float oneFloatWrapper;
    private Integer oneIntWrapper;
    private Long oneLongWrapper;
    private Short oneShortWrapper;

    public Boolean getOneBoolWrapper() {
        return oneBoolWrapper;
    }

    public void setOneBoolWrapper(Boolean oneBoolWrapper) {
        this.oneBoolWrapper = oneBoolWrapper;
    }

    public Byte getOneByteWrapper() {
        return oneByteWrapper;
    }

    public void setOneByteWrapper(Byte oneByteWrapper) {
        this.oneByteWrapper = oneByteWrapper;
    }

    public Character getOneCharWrapper() {
        return oneCharWrapper;
    }

    public void setOneCharWrapper(Character oneCharWrapper) {
        this.oneCharWrapper = oneCharWrapper;
    }

    public Double getOneDoubleWrapper() {
        return oneDoubleWrapper;
    }

    public void setOneDoubleWrapper(Double oneDoubleWrapper) {
        this.oneDoubleWrapper = oneDoubleWrapper;
    }

    public Float getOneFloatWrapper() {
        return oneFloatWrapper;
    }

    public void setOneFloatWrapper(Float oneFloatWrapper) {
        this.oneFloatWrapper = oneFloatWrapper;
    }

    public Integer getOneIntWrapper() {
        return oneIntWrapper;
    }

    public void setOneIntWrapper(Integer oneIntWrapper) {
        this.oneIntWrapper = oneIntWrapper;
    }

    public Long getOneLongWrapper() {
        return oneLongWrapper;
    }

    public void setOneLongWrapper(Long oneLongWrapper) {
        this.oneLongWrapper = oneLongWrapper;
    }

    public Short getOneShortWrapper() {
        return oneShortWrapper;
    }

    public void setOneShortWrapper(Short oneShortWrapper) {
        this.oneShortWrapper = oneShortWrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BeanWithWrapperProperties that = (BeanWithWrapperProperties) o;
        return Objects.equals(oneBoolWrapper, that.oneBoolWrapper) &&
                Objects.equals(oneByteWrapper, that.oneByteWrapper) &&
                Objects.equals(oneCharWrapper, that.oneCharWrapper) &&
                Objects.equals(oneDoubleWrapper, that.oneDoubleWrapper) &&
                Objects.equals(oneFloatWrapper, that.oneFloatWrapper) &&
                Objects.equals(oneIntWrapper, that.oneIntWrapper) &&
                Objects.equals(oneLongWrapper, that.oneLongWrapper) &&
                Objects.equals(oneShortWrapper, that.oneShortWrapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), oneBoolWrapper, oneByteWrapper, oneCharWrapper, oneDoubleWrapper, oneFloatWrapper, oneIntWrapper, oneLongWrapper, oneShortWrapper);
    }
}
