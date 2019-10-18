package javabean.serializer;

import java.util.ArrayList;
import java.util.List;

public class CycledReferenceAnalyzer {
    private CycledReferenceAnalyzer parent;
    private Object thisObject;

    public CycledReferenceAnalyzer(CycledReferenceAnalyzer parent, Object thisObject) {
        this.parent = parent;
        this.thisObject = thisObject;
    }

    public CycledReferenceAnalyzer(Object thisObject) {
        this.parent = null;
        this.thisObject = thisObject;
    }

    public Object getThisObject() {
        return thisObject;
    }

    public boolean hasCyclicReferences() {
        CycledReferenceAnalyzer parentObject = parent;
        while (parentObject != null) {
            if (parentObject.thisObject != this.thisObject) {
                parentObject = parentObject.parent;
            } else {
                return true;
            }
        }
        return false;
    }
}
