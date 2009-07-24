package mini.java.syntaxtree;

import mini.java.semantics.TypeVisitor;
import mini.java.semantics.Visitor;

public class ArrayLookup extends Exp {
	public Exp e1, e2;

	public ArrayLookup(Exp ae1, Exp ae2) {
		super(ae1.line);
		e1 = ae1;
		e2 = ae2;
	}

	public void accept(Visitor v) {
		v.visit(this);
	}

	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	public String toString() {
		return e1 + "[" + e2 + "]";
	}
}
