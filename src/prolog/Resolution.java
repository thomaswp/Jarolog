package prolog;

import java.util.HashMap;
import java.util.Map;

/** Allows you to draw conclusions on a given database Frame */
public class Resolution {
	
	private Frame frame;
	
	public Resolution(Frame frame) {
		this.frame = frame;
	}
	
	public boolean resolve(Predicate query, final Response res) {
		boolean r = false;
		// check every predicate in the database and attempt to resolve
		for (Predicate possible : frame.predicates) {
			r |= resolve(possible, query, res);
		}
		return r;
	}
	
	// check a query against a predicate in the database frame
	private boolean resolve(final Predicate pFrame, final Predicate pQuery, final Response res) {
		// if this predicate has the wrong name of number of arguments, return false
		if (!pFrame.name.equals(pQuery.name)) return false;
		if (pFrame.terms.size() != pQuery.terms.size()) return false;
		
		// substitutions to make the query match the predicate
		final Substitution querySub = new Substitution();
		// substitutions to make the predicate match the query
		Substitution frameSub = new Substitution();
		// variable substitutions to make their repeated variables match
		HashMap<String, String> variableSub = new HashMap<String, String>();
		
		for (int i = 0; i < pFrame.terms.size(); i++) {
			Term tFrame = pFrame.terms.get(i); // database predicate
			Term tQuery = pQuery.terms.get(i); // query predicate
			
			if (tFrame instanceof Constant && tQuery instanceof Constant) {
				if (!tFrame.name.equals(tQuery.name)) return false; // two constants must match
			} else if (tQuery instanceof Variable && tFrame instanceof Constant) {
				// substitute a constant in the predicate for a variable in the query
				String original = querySub.put(tQuery.name, tFrame.name);
				// but make sure not conflicting substitution has not already been made
				if (original != null && !original.equals(tFrame.name)) return false;
			} else if (tQuery instanceof Constant && tFrame instanceof Variable) {
				// do the reverse for the query's constants
				String original = frameSub.put(tFrame.name, tQuery.name);
				if (original != null && !original.equals(tQuery.name)) return false;
			} else {
				// bind query variables if they're repeated and the previous instance has been bound
				boolean bound = false;
				for (Map.Entry<String, String> entry : frameSub.entrySet()) {
					if (entry.getKey().equals(tFrame.name)) {
						querySub.put(tQuery.name, entry.getValue());
						bound = true;
						break;
					}
				}
				
				if (!bound) {
					// otherwise keep track of which variables are replacing which
					String sub = variableSub.get(tQuery.name); 
					if (sub != null) {
						// and if we see the same one twice, use the same substitution
						frameSub.put(sub, tFrame.name);
					} else {
						variableSub.put(tQuery.name, tFrame.name);
					}
				}
			}
		}
		
		// response callback for successful matches
		Response condResp = new Response() {
			@Override
			public void resolved(Substitution sub) {
				// we start with a copy of the substitutions for the query
				Substitution copy = querySub.copy(); 
				for (Map.Entry<String, String> entry : sub.entrySet()) {
					// and we add any relevant substitutions necessary for making the conditions match
					for (int i = 0; i < pFrame.terms.size(); i++) {
						Term tFrame = pFrame.terms.get(i);
						Term tQuery = pQuery.terms.get(i);
						if (!(tQuery instanceof Variable && tFrame instanceof Variable)) continue;
						if (tFrame.name.equals(entry.getKey())) {
							copy.put(tQuery.name, entry.getValue());
							break;
						}
					}
				}
				res.resolved(copy);
			}
		};
		
		// make sure the conditions match
		return resolveConditions(pFrame, frameSub, 0, condResp);
	}

	// resolve a predicate's conditions to make sure it's valid under the given substitution
	private boolean resolveConditions(final Predicate pFrame, final Substitution sub, 
			final int subIndex, final Response res) {
		if (subIndex >= pFrame.conditions.size()) {
			// if this is the last condition, resolve successfully
			res.resolved(sub);
			return true;
		}
		
		// get the condition and make the required substitutions
		Predicate condition = pFrame.conditions.get(subIndex);
		condition = condition.substitute(sub);
		
		final Flag flag = new Flag(false);
		
		// see if we can't resolve that condition as a new query in the database
		Response resResp = new Response() {
			@Override
			public void resolved(Substitution s) {
				// if we can, add the necessary conditions and resolve the rest of the predicate's conditions
				Substitution nextSub = sub.plus(s);
				flag.value |= resolveConditions(pFrame, nextSub, subIndex + 1, res);
			}
		};
		resolve(condition, resResp); 
		return flag.value;
	}
	
	/** resolved will be called once for every possible valid substitution */
	public interface Response {
		void resolved(Substitution sub);
	}
	
	private static class Flag {
		public boolean value;
		public Flag(boolean value) { this.value = value; }
	}
}
