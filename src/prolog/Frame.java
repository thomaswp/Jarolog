package prolog;

import java.util.ArrayList;
import java.util.List;


public class Frame {
	List<Predicate> predicates = new ArrayList<Predicate>();
	int tries = 0;
	
	void add(Predicate predicate) {
		predicates.add(predicate);
	}
}
