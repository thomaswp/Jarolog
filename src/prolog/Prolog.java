package prolog;


public class Prolog {
	public Prolog() {
		diabetes();
	}
	
	public static void main(String args[]) {
		new Prolog();
	}
	
	void diabetes() {
		// build the database
		Constant david = new Constant(Names.david);
		Constant diabetics = new Constant(Names.diabetics);
		Constant candy = new Constant(Names.candy);
		Constant snickers = new Constant(Names.snickers);
		Constant sugar = new Constant(Names.sugar);
		Constant isa = new Constant(Names.isa);
		Constant ako = new Constant(Names.ako);
		Constant shouldAvoid = new Constant(Names.shouldAvoid);
		Constant contains = new Constant(Names.contains);
		
		Frame startFrame = new Frame();
		
		startFrame.add(new Predicate(Names.f_edge, david, isa, diabetics));
		startFrame.add(new Predicate(Names.f_edge, diabetics, shouldAvoid, sugar));
		startFrame.add(new Predicate(Names.f_edge, candy, contains, sugar));
		startFrame.add(new Predicate(Names.f_edge, snickers, ako, candy));
		
		Variable node = new Variable("Node");
		Variable slot = new Variable("Slot");
		Variable value = new Variable("Value");
		Variable node1 = new Variable("Node1");
		Variable stuff = new Variable("Stuff");
		
		startFrame.add(new Predicate(Names.f_value, node, slot, value)
		.addCondition(new Predicate(Names.f_edge, node, slot, value)));
		
		startFrame.add(new Predicate(Names.f_value, node, slot, value)
		.addCondition(new Predicate(Names.f_edge, node, isa, node1))
		.addCondition(new Predicate(Names.f_value, node1, slot, value)));
		
		startFrame.add(new Predicate(Names.f_value, node, slot, value)
		.addCondition(new Predicate(Names.f_edge, node, ako, node1))
		.addCondition(new Predicate(Names.f_value, node1, slot, value)));
		
		startFrame.add(new Predicate(Names.f_value, node, shouldAvoid, node1)
		.addCondition(new Predicate(Names.f_value, node1, contains, stuff))
		.addCondition(new Predicate(Names.f_value, node, shouldAvoid, stuff)));
		
		// start an interpreter to take queries from the user
		Interpreter interpreter = new Interpreter(startFrame);
		interpreter.start();
	}
}
