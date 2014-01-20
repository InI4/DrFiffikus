import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;

public class HtmlWriter
	implements Writer
{
	private PrintWriter pw;
	private List<Integer> numbers = new ArrayList<Integer>();

	public HtmlWriter(String fName, String msg)
		throws IOException
	{
		pw = new PrintWriter(new BufferedWriter(new FileWriter(fName)));
		o("<html><head>");
		o("<title>"+msg+"</title>");

		o("<style type='text/css'>");
		o(".b { border-style: solid; border-width: 1pt; border-color:blue; }");
		o(".p { padding: 2em; }");
		o(".a { background-image: url(db.png); background-size: 100% auto; background-repeat: no-repeat; width: 70; }");
		o(".s { font-size: 50%; color: #444; horizontal-align: right;}");
		o("</style>");
		o("</head><body><h1>Doktor Fiffikus</h1><table height='84%' class='p'><tr>");
	}

	private int number = 0;

	public void start() throws IOException
	{
		o("<td>&nbsp;</td><td valign='top'><table class='a b p'>");
	}
	public void write(DrGruebel.SingleStep step) throws IOException
	{
		if ( step.getOperation() == DrGruebel.START_NOOP ) {
			o("<tr><td colspan='2' style='height: 20; width: 60'><b>"+step.getOperator()+"</b></td></tr>");
			o("<tr><td colspan='2' style='height: 30; '>&nbsp;</td></tr>");
		}
		else {
			o("<tr><td>"+step.getOperation().getChar()+"</td><td>"+step.getOperator()+"</td></tr>");
		}
		number = step.apply(number);
	}
	public void end() throws IOException
	{
		numbers.add(number);
		o("</table></td>");
		pw.flush();
	}
	public void close() throws IOException
	{
		o("<td>&nbsp;</td></tr>");
		o("<tr><td>&nbsp;</td></tr>");
		o("<tr><td>&nbsp;</td></tr>");
		o("<tr><td></td>");
		int max = 0;
		for(Integer number : numbers) max = Math.max(max, number);
		int len = Integer.toString(max).length();
		for(Integer number : numbers) {
			String nu = Integer.toString(number);
			o("<td></td><td class='s'>");
			for(int i = nu.length(); i < len; i++) pw.write('0');
			o(nu+"</td>");
		}
		o("</tr></table></body></html>");
		pw.close();
	}

	private void o(String s) throws IOException { pw.println(s); }
}
