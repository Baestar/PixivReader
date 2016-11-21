import java.util.Scanner;

import com.jaunt.JauntException;

public class Main {

	public static void main(String[] args) throws JauntException
	{
		PixivReader pR = new PixivReader(args[0], args[1]); //first arg User second arg Pass
		Thread processer = new Thread(pR);
		processer.start();
		Scanner scanner = new Scanner(System.in);
	    while(true)
	    {
			String cmd = scanner.next();
		    if(cmd.equals("q"))
		    	break;
		    pR.addToFetch(cmd);

	    }
	    processer.interrupt();
	    scanner.close();
	}

}
