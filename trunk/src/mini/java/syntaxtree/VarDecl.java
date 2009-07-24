package mini.java.syntaxtree;

import mini.java.semantics.TypeVisitor;
import mini.java.semantics.Visitor;

public class VarDecl {
	public Type t;
	public Identifier i;

	public VarDecl(Type at, Identifier ai) {
		t = at;
		i = ai;
	}

	public void accept(Visitor v) {
		v.visit(this);
	}

	public Type accept(TypeVisitor v) {
		return v.visit(this);
	}
	
	public String toString() {
		return t + " " + i;
	}
}
