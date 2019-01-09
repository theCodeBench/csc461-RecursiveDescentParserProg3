import java.io.*;
import rules.*;
import java.util.Objects.*;

/**
 * Parser class
 * Parses an expression using EBNF grammar, outputting if the 
 * expression is valid or not.
 *
 * @author Soham Naik, Ryan Hinrichs
 */
public class Parser
{    
    /**
     * Takes in expressions to be parsed until an empty line is entered
     * @param args Arguments provided by the user, specifically "-t" can
     *             be passed so all the tokens are outputted.
     * @throws IOException No error will be thrown for reading or writing to 
     * memory
     */
    public static void main( String [] args ) throws IOException
    {
        Parser par = new Parser();
        
        // Input stream reader to read input from user
        BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
        
        // checking for token argument
        if(args.length != 0 && !args[0].equals("-t"))
        {
            System.out.println("Incorrect usage.");
            System.out.println("Correct usage is: java Parser [-t]");
            return;
        }
        
        System.out.println( "%java Parser" );
        System.out.print("Enter expression: ");
        
        String orig_expr = cin.readLine();
        
        // keeps going until blank line is entered
        while (!orig_expr.equals(""))
        {
                        
            Grammar rulescheck = new Grammar( orig_expr );
            
            // basic if-else contruct which tells if the string is valid or 
            // not based on value returned by check_validity funtion
            
            int c = rulescheck.check_validity(rulescheck);
            
            // prints input string
            rulescheck.print();
            
            if(c == 0)
                System.out.print(" is a valid expression\n");
            else
                System.out.print(" is not a valid expression\n");
            
            if(args.length != 0 && args[0].equals("-t"))
            {
                rulescheck.tokenprint();
            }

            System.out.print("\n\nEnter expression: ");
            orig_expr = cin.readLine();
        }
        
        // end of input
        System.out.println("(end of input)");
    }
} 
