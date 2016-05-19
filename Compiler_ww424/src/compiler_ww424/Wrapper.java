package compiler_ww424;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {
    public Wrapper(List<String> list, StringWrapper methods) {
        this.field = list;
        this.method = methods;
     }

     public List<String> getFields() { return this.field; }
     public StringWrapper getMethods() { return this.method; }

     private List<String> field;
     private StringWrapper method;
}
