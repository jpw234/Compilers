package compiler_ww424;

import java.util.List;

public class StringWrapper {
	public StringWrapper(List<String> method, List<String> manglingMethod) {
        this.method = method;
        this.manglingMethod = manglingMethod;
     }

     public List<String> getMethod() { return this.method; }
     public List<String> getMangleMethod() { return this.manglingMethod; }

     private List<String> method;
     private List<String> manglingMethod;
}
