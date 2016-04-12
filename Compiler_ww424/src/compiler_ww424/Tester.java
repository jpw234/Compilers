package compiler_ww424;
import java.util.ArrayList;

public class Tester {
	public static void main(String[] args) {
		ArrayList<Type> in = new ArrayList<Type>();
		ArrayList<Type> out = new ArrayList<Type>();
		
		System.out.println(FunCall.mangle_name("multiple__underscores",
				new FunType(new Tuple(in), new Tuple(out))));
	}
}
