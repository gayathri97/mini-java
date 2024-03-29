package mini.java.syntaxtree;

import mini.java.semantics.TypeVisitor;
import mini.java.semantics.Visitor;

public class PrioExp extends Exp {

	public Exp e;

	public PrioExp(Exp ae) {
		super(ae.line);
		e = ae;
	}
	
	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
	}

	@Override
	public Type accept(TypeVisitor v) {
		// TODO Auto-generated method stub
		return v.visit(this);
	}

	public String toString() {
		return "(" + e + ")";
	}
}
