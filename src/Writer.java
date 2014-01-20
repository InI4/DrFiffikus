import java.io.IOException;

public interface Writer
{
	public void start() throws IOException;
	public void write(DrGruebel.SingleStep step) throws IOException;
	public void end() throws IOException;
	public void close() throws IOException;

	public final static Writer STDOUT = new Writer() {
		public void start() { };
		public void write(DrGruebel.SingleStep step) {
			System.out.format(" %c %d\n", step.getOperation().getChar(), step.getOperator());
	}
		public void end() { System.out.println(); };
		public void close() { } ;
	};
}
