package prolog;

import java.util.ArrayList;
import java.util.Scanner;

import prolog.Resolution.Response;

public class Interpreter {
	Frame frame;
	
	public Interpreter(Frame frame) {
		this.frame = frame;
	}
	
	public void start() {
		Resolution resolution = new Resolution(frame);
		
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.print("Enter a query using prolog syntax (\"quit\" to exit): ");
			String line = sc.nextLine();
			if (line.equalsIgnoreCase("quit")) break;
			
			try {
				// parse the query
				int index = line.indexOf("(");
				String name = line.substring(0, index);
				name = name.trim();
				String[] arguments = line.substring(index).split(",");
				Term[] terms = new Term[arguments.length];
				index = 0;
				for (String argument : arguments) {
					argument = argument.replaceAll(",", "").replaceAll(" ", "").replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\\.", "");
					if (Character.isUpperCase(argument.charAt(0))) {
						terms[index++] = new Variable(argument);
					} else {
						terms[index++] = new Constant(argument);
					}
				}
				
				// query the database and add successful substitutions to the list
				Predicate query = new Predicate(name, terms);
				final ArrayList<Substitution> subs = new ArrayList<Substitution>();
				boolean success = resolution.resolve(query, new Response() {
					@Override
					public void resolved(Substitution sub) {
						subs.add(sub);
					}
				});
				
				if (success) {
					// print them
					boolean subbed = false;
					for (Substitution sub : subs) {
						int ts = 0;
						for (Term term : terms) {
							if (term instanceof Variable) {
								String replace = sub.get(term.name);
								if (replace == null) continue;
								System.out.printf("%s = %s\n", term.name, replace);
								subbed = true;
								ts++;
							}
						}
						if (ts > 0) System.out.println();
					}
					if (!subbed) {
						System.out.println("true");
					}
				} else {
					System.out.println("false");
				}
				
			} catch (Exception e) {
				System.out.println("Improper syntax. Try something like: value(david, shouldAvoid, X).");
			}
		}
	}
}
