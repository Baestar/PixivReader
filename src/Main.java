import com.jaunt.JauntException;

public class Main {

	public static void main(String[] args) 
	{
		PixivReader pV = new PixivReader(args[0], args[1]); //first arg User second arg Pass
		
		try
		{

            for(int i = 2; i< args.length; i++)
            {
                pV.parseArgs(args[i]);
            }
            
        }
        catch(JauntException e)
        {
            System.err.println(e);
        }

	}

}
