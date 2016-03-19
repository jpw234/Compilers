package compiler_ww424;

import java.util.ArrayList;

public class FunType extends Type {
	private Tuple inputs;
	private Tuple outputs;
	
	public FunType(Tuple in, Tuple out) {
		super("fun");
		inputs = in; outputs = out;
	}
	
	public Tuple getInputs() {
		
		return inputs;
	}
	
	public Tuple getOutputs() {
		
		return outputs;
	}
	
	@Override
	public boolean equals(Type f) {
		if(f.getType() != "fun") return false;
		
		FunType ft = (FunType) f;
		
		if(inputs.equals(ft.getInputs()) && outputs.equals(ft.getOutputs())) {
			return true;
		}
		else return false;
	}
}
