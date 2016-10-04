package prolog;

import java.util.HashMap;
import java.util.Map;


public class Substitution extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Map.Entry<String, String> entry : entrySet()) {
			if (sb.length() > 1) sb.append(", ");
			sb.append(entry.getValue());
			sb.append("/");
			sb.append(entry.getKey());
		}
		sb.append("}");
		return sb.toString();
	}

	/** Returns a copy of this Substitution with the given one appended */
	public Substitution plus(Substitution s) {
		Substitution sub = new Substitution();
		sub.add(this);
		sub.add(s);
		return sub;
	}
	
	public Substitution copy() {
		Substitution sub = new Substitution();
		sub.add(this);
		return sub;
	}
	
	/** Appends the given substitution (modifying this one) */
	public void add(Substitution s) {
		for (Map.Entry<String, String> entry : s.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
