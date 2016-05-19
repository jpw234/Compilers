package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {
    public Wrapper(List<String> list, List<String> list2) {
        this.field = list;
        this.method = list2;
     }

     public List<String> getFields() { return this.field; }
     public List<String> getMethods() { return this.method; }

     private List<String> field;
     private List<String> method;
}
