package mini.java.syntaxtree;

import java.util.Vector;

public class MethodDeclList {
	private Vector<MethodDecl> list;

	public MethodDeclList() {
		list = new Vector<MethodDecl>();
	}

	public void addElement(MethodDecl n) {
		list.addElement(n);
	}

	public MethodDecl elementAt(int i) {
		return list.elementAt(i);
	}

	public int size() {
		return list.size();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(MethodDecl md : list) {
			sb.append(md.toString() + "\n");
		}
		
		return sb.toString();
	}
}
