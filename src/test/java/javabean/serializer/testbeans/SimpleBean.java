package javabean.serializer.testbeans;

public class SimpleBean {
    private int id;
    private boolean valid;
    private String name;
    private Object unreadableObject;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
