package prolog;

import java.util.ArrayList;
import java.util.List;


public class Predicate {
	String name;
	List<Term> terms = new ArrayList<Term>();
	List<Predicate> conditions = new ArrayList<Predicate>();
	
	
	public Predicate(String name, Term... terms) {
		this.name = name;
		for (Term term : terms) this.terms.add(term);
	}
	
	public Predicate(String name, List<Term> terms) {
		this.name = name;
		this.terms = terms;
	}
	
	public Predicate addCondition(Predicate predicate) {
		this.conditions.add(predicate);
		return this;
	}
	
	/** Returns a copy of this predicate, with the given substitution applied */
	public Predicate substitute(Substitution sub) {
		if (sub.size() == 0) {
			return this;
		}
		
		Predicate p = new Predicate(name, substitute(terms, sub));
		for (Predicate cond : conditions) {
			p.addCondition(cond.substitute(sub));
		}
		return p;
	}
	
	private List<Term> substitute(List<Term> list, Substitution sub) {
		List<Term> nList = new ArrayList<Term>();
		for (Term term : list) {
			nList.add(term);
			if (!(term instanceof Variable)) continue;
			String replace = sub.get(term.name);
			// while loop to handle transitive substitutions like {X/Y, z/X}
			while (replace != null && replace.length() > 0) {
				Term t = Character.isUpperCase(replace.charAt(0)) ? 
						new Variable(replace) : new Constant(replace);
				nList.set(nList.size() - 1, t);
				replace = sub.get(replace);
			}
		}
		return nList;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(");
		int i = 0;
		for (Term t : terms) {
			if (i++ > 0) sb.append(", ");
			sb.append(t);
		}
		sb.append(")");
		if (conditions.size() != 0) {
			sb.append(" :- ");
			i = 0;
			for (Predicate p : conditions) {
				if (i++ > 0) sb.append(", ");
				sb.append(p);
			}
		}
		return sb.toString();
	}
}
