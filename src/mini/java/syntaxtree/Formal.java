package mini.java.syntaxtree;

import mini.java.semantics.TypeVisitor;
import mini.java.semantics.Visitor;

public class Formal {
	public Type t;
	public Identifier i;

	public Formal(Type at, Identifier ai) {
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
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Formal)) {
			return false;
		} else {
			Formal f = (Formal)o;
			return f.t.equals(this.t) && f.i.s.equals(this.i.s);
		}
	}
}
