package prolog;


public abstract class Term {
	String name;
	
	public Term(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
