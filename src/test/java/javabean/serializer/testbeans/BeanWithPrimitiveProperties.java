package javabean.serializer.testbeans;

import java.util.Objects;

public class BeanWithPrimitiveProperties {
    private boolean oneBool;
    private byte oneByte;
    private char oneChar;
    private double oneDouble;
    private float oneFloat;
    private int oneInt;
    private long oneLong;
    private short oneShort;

    public boolean isOneBool() {
        return oneBool;
    }

    public void setOneBool(boolean oneBool) {
        this.oneBool = oneBool;
    }

    public byte getOneByte() {
        return oneByte;
    }

    public void setOneByte(byte oneByte) {
        this.oneByte = oneByte;
    }

    public char getOneChar() {
        return oneChar;
    }

    public void setOneChar(char oneChar) {
        this.oneChar = oneChar;
    }

    public double getOneDouble() {
        return oneDouble;
    }

    public void setOneDouble(double oneDouble) {
        this.oneDouble = oneDouble;
    }

    public float getOneFloat() {
        return oneFloat;
    }

    public void setOneFloat(float oneFloat) {
        this.oneFloat = oneFloat;
    }

    public int getOneInt() {
        return oneInt;
    }

    public void setOneInt(int oneInt) {
        this.oneInt = oneInt;
    }

    public long getOneLong() {
        return oneLong;
    }

    public void setOneLong(long oneLong) {
        this.oneLong = oneLong;
    }

    public short getOneShort() {
        return oneShort;
    }

    public void setOneShort(short oneShort) {
        this.oneShort = oneShort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanWithPrimitiveProperties that = (BeanWithPrimitiveProperties) o;
        return oneBool == that.oneBool &&
                oneByte == that.oneByte &&
                oneChar == that.oneChar &&
                Double.compare(that.oneDouble, oneDouble) == 0 &&
                Float.compare(that.oneFloat, oneFloat) == 0 &&
                oneInt == that.oneInt &&
                oneLong == that.oneLong &&
                oneShort == that.oneShort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oneBool, oneByte, oneChar, oneDouble, oneFloat, oneInt, oneLong, oneShort);
    }
}
