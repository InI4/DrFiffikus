import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class DrGruebel
{
	public interface Operation {
		public int apply(int previous, int next);
		public boolean doesntWork(int previous, int next);
		public char getChar();
		public boolean isPoint();
	}

	public final static Operation START_NOOP = new Operation() {
		public int apply(int previous, int next) { return next; }
		public boolean doesntWork(int previous, int next) { return false; }
		public char getChar() { return ' '; }
		public boolean isPoint() { return false; }
	};

	public final static Operation PLUS = new Operation() {
		public int apply(int previous, int next) { return previous + next; }
		public boolean doesntWork(int previous, int next) { return false; }
		public char getChar() { return '+'; }
		public boolean isPoint() { return false; }
	};

	public final static Operation POSITIVE_MINUS = new Operation() {
		public int apply(int previous, int next) { return previous - next; }
		public boolean doesntWork(int previous, int next) { return previous <= next; }
		public char getChar() { return '-'; }
		public boolean isPoint() { return false; }
	};

	public final static Operation TIMES = new Operation() {
		public int apply(int previous, int next) { return previous * next; }
		public boolean doesntWork(int previous, int next) { return previous == 1 || next == 1; }
		public char getChar() { return 'x'; }
		public boolean isPoint() { return true; }
	};

	public final static Operation INTEGER_DIVIDE = new Operation() {
		public int apply(int previous, int next) { return previous / next; }
		public boolean doesntWork(int previous, int next) { return next == 1 || next == previous || previous % next != 0; }
		public char getChar() { return ':'; }
		public boolean isPoint() { return true; }
	};

	private Operation[] usedOps = new Operation[] { PLUS, POSITIVE_MINUS, TIMES, INTEGER_DIVIDE };
	private Random rand;
	private int maximumNumber, maximumPointNumber;

	public DrGruebel(int maximumNumber, int maximumPointNumber, long seed)
	{
		this.maximumNumber = maximumNumber;
		this.maximumPointNumber = maximumPointNumber;
		rand = new Random(seed);
	}

	public class SingleStep
	{
		private Operation operation;
		private int operator;
		public SingleStep(Operation operation, int operator)
		{
			this.operation = operation;
			this.operator = operator;
		}

		public int getOperator() { return operator; }

		public Operation getOperation() { return operation; }

		public int apply(int previous) { return operation.apply(previous, operator); }
	}

	public List<SingleStep> generateProblem(int steps)
	{
		ArrayList<SingleStep> problem = new ArrayList<>(steps+1);
		while ( true ) {
			int currentNumber = 1 + random(maximumNumber); 
			SingleStep s;
			s = new SingleStep(START_NOOP, currentNumber);
			problem.add(s);
			while ( problem.size() <= steps ) {
				s = createStep(s.getOperation(), currentNumber);
				problem.add(s);
				currentNumber = s.apply(currentNumber);
			}
			if ( isProblemAcceptable(problem) ) return problem;
			problem.clear();
		}
	}

	private SingleStep createStep(Operation lastOp, int currentNumber) {
		Operation op;
		int opn;
		while(true) {
			Operation trialOp = usedOps[random(usedOps.length)];
			if ( trialOp == lastOp ) continue;
			opn = 1 + random(limitNumber(trialOp));
			if ( trialOp.doesntWork(currentNumber, opn) ) continue;
			int res = trialOp.apply(currentNumber, opn);
			if ( res > maximumNumber ) continue;
			return new SingleStep(trialOp, opn);
		}
	}

	public boolean isProblemAcceptable(List<SingleStep> problem) {
		HashMap<Operation, Integer> ops = new HashMap<>();
		for(SingleStep step : problem) {
			Operation op = step.getOperation();
			Integer count = ops.get(op);
			if ( count == null ) count = Integer.valueOf(1);
			else count = Integer.valueOf(1 + count.intValue());
			ops.put(op, count);
		}
		// Strange counting, cause we allways start with a START_NOOP!
		if ( ops.size() >= problem.size() ) return true;
		// If we have enough steps, we want a step of every type!!!
		if ( ops.size() <= usedOps.length ) return false;
		int h = (problem.size()-1) / usedOps.length;
		if ( h == 1 ) return true;
		for(Operation op : ops.keySet()) if ( op != START_NOOP && ops.get(op) < h ) return false;
		return true;
	}

	public int limitNumber(Operation op) { return op.isPoint() ? maximumPointNumber : maximumNumber; }

	private int random(int limit) { return rand.nextInt(limit); }

	public static void write(List<SingleStep> problem, Writer w)
		throws IOException
	{
		w.start();
		for(SingleStep s : problem) w.write(s);
		w.end();
	}

	public static void main(String[] args)
		throws Exception
	{
		int maximumNumber = Integer.parseInt(args[0]);
		int maximumPointNumber = Integer.parseInt(args[1]);
		int steps = Integer.parseInt(args[2]);
		long seed = args.length > 3 ? Integer.parseInt(args[3]) : System.currentTimeMillis();
		int times = args.length > 4 ? Integer.parseInt(args[4]) : 1;
		DrGruebel dg = new DrGruebel(maximumNumber, maximumPointNumber, seed); 
		Writer w = new HtmlWriter("out.html", "Seed="+seed);
		for(int i = 0; i < times; i++) {
			List<SingleStep> problem = dg.generateProblem(steps);
			DrGruebel.write(problem, Writer.STDOUT);
			DrGruebel.write(problem, w);
		}
		w.close();
	}
}
