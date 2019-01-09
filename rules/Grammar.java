package rules;
import java.io.*;
import javafx.util.Pair;
import java.util.ArrayList;

/**
 * Grammar class
 * This class contains all the EBNF rules given in the document. It takes in
 * the expression string entered by the user and assigns each character in the
 * string to a value. For example '+' character will have the number 40 assigned
 * to it. This list of key-value pairs is then passed into the 'check_validity'
 * function which checks if it's valid or not. 'Check_validity' then calls 
 * 'checkexpr' function which recursilvely checks the expression.
 * 
 * @author Soham Naik, Ryan Hinrichs
 *
 */
public class Grammar
{
    // numerical representations of different operation classifications
    int eof = 0;    
    int integer = 1;
    int flt = 2;
    int id = 3;
    int plus = 40;
    int min = 50;
    int mul = 60;
    int div = 70;
    int mod = 80;
    int lparen = 90;
    int rparen = 9;
    int space = 4;
    int undef = -1;
    private int location = -1;
    private int classif;
    private int prevclassif = -2;
    private int fullclassif;
    private String eval = "";
    private ArrayList<Pair <String,Integer> > list;
    
    // counter keeps track of the validity of a expression
    // 1 for not valid and 0 for valid
    int counter = 0; 
    // loop variable which goes through the expression
    int loopvar = 0; 
    
    /**
     * Constructor function for the Grammar class.  
     * @param expr expression to be parsed.
     */
    public Grammar( String expr )
    {
        // list which holds the keys and values
        list = new ArrayList < Pair <String,Integer> > ();
        
        // goes through each character in the entered string
        for (char ch : expr.toCharArray())
        {
            //Classification of a single character
            classif = parseChar(ch);
            //Special case for first character after a space or int, flt, id
            if(prevclassif == -2 || (classif == fullclassif && classif <9))
            {
                eval += ch;
                fullclassif = classif;
            }//Special case for space, stops the token and starts over
            else if(classif == space)
            {
                if(prevclassif != space)
                    list.add(new Pair <String,Integer> (eval, fullclassif));

                eval = "";
                fullclassif = space;
            }//Special case for an integer that is part of an id
            else if(fullclassif == id && classif == integer)
            {
                eval += ch;
            }//SPecial case for an integer that becomes a float
            else if(fullclassif == integer && classif == flt)
            {
                eval += ch;
                fullclassif = flt;
            }//Adds integers to a float
            else if(fullclassif == flt && classif == integer)
            {
                eval += ch;
            }//Special case for if a minus is at the front of an integer
            else if(fullclassif == min && classif == integer 
                && (list.size() == 0 || list.get(list.size()-1).getValue() > 9))
            {
                eval += ch;
                fullclassif = flt;
            }//Special case for if a minus is at the front of an id
            else if(fullclassif == min && classif == id
                && (list.size() == 0 || list.get(list.size()-1).getValue() > 9))
            {
                eval += ch;
                fullclassif = id;
            }//Special case for if a minus is in front of a lparen
            else if(fullclassif == min && classif == lparen
                && (list.size() == 0 || list.get(list.size()-1).getValue() > 9))
            {
                eval += ch;
                fullclassif = lparen;
            }
            else
            {
                if(fullclassif != space)
                {
                    list.add(new Pair <String,Integer> (eval, fullclassif));
                }

                eval = "";
                eval += ch;
                fullclassif = classif;
            }
            prevclassif = classif;
        }
         
        //Adds the last token to the list
        list.add(new Pair <String,Integer> (eval, fullclassif));
    }

    /**
     * Parses each character int othe expression into their individual 
     * parts.
     * @param c character that is being parsed
     * @return int corresponding value code to the character
     */
    private int parseChar(char c)
    {
        if(Character.getNumericValue(c) < 10 && Character.getNumericValue(c) >= 0)
            return integer;
        else if(c == '.')
            return flt;
        else if(c == '+')
            return plus;
        else if(c == '-')
            return min;
        else if(c == '*')
            return mul;
        else if(c == '/')
            return div;
        else if(c == '%')
            return mod;
        else if(c == '(')
            return lparen;
        else if(c == ')')
            return rparen;
        else if(c == ' ')
            return space;
        else if (Character.isLetter(c))
            return id;
        else
            return undef;
    }
    
    /**
     * Returns the size of the list. For public use by other classes.
     * @return int size of the list
     */
    public int size()
    {
        return list.size();
    }

    /**
     * Prints the expression with spaces between characters, except 
     * directly after a left parenthesis or before a right 
     * paranthesis. 
     */
    public void print()
    {
        System.out.print("\"");
        
        for( int i = 0; i < list.size()-1; i++)
        {
            System.out.print(list.get(i).getKey());
            
            if( list.get(i).getValue() != lparen && 
                list.get(i+1).getValue() != rparen)
            {
                System.out.print(" ");
            }
        }

        System.out.print(list.get(list.size()-1).getKey() + "\"");
    }

    /**
     * Prints the individual tokens of the expression, separated by 
     * commas.  If the character is float, integer, or id, it is sent
     * into the retokenize function to be split on negatives and periods.
     */
    public void tokenprint()
    {
        int val;
        System.out.print("tokens: ");   
        for(int i = 0; i < list.size()-1; i++){
            val = list.get(i).getValue();
            if(val != integer && val != flt && val != id && val != lparen)
                System.out.print(list.get(i).getKey());
            else
                retokenize(list.get(i).getKey());
            System.out.print(",");
        }

        val = list.get(list.size()-1).getValue();
        if(val != integer && val != flt && val != id)
            System.out.print(list.get(list.size()-1).getKey());
        else
            retokenize(list.get(list.size()-1).getKey());
    }

    /**
     * Splits the character string on any negatives or periods.
     * @param s string from expression to be split
     */
    public void retokenize(String s)
    {
        int pointloc;
        int start = 0;

        if(s.charAt(0) == '-') 
        { 
            System.out.print("-,");
            start++;
        }
        
        pointloc = s.indexOf('.');
        if(pointloc != -1)
        {
            System.out.print(s.substring(start,pointloc) + ",.,");
            System.out.print(s.substring(pointloc+1));
        }else
            System.out.print(s.substring(start));
    }
    
    /**
     * Parses Strings in the language for factor
     * @param g Object of class Grammar
     */
    public void checkfactor(Grammar g)
    {
        // if loop variable exceed the list size, it exits
        if (g.loopvar == list.size())
            return;
            
        // checks for integer, float or id
        if (list.get(g.loopvar).getValue() == integer || 
            list.get(g.loopvar).getValue() == flt ||
            list.get(g.loopvar).getValue() == id)
        {
            // increments loop variable
            g.loopvar += 1;
            // expression is valid so far
            g.counter = 0;
        }
        else
        {
            // checks for left parentheses
            if ( list.get(g.loopvar).getValue() == lparen)
            {
                g.loopvar += 1;
                // expr comes after '('
                checkexpr(g);
                // checks for ')' after expr
                if ( (g.loopvar != list.size()) &&   
                     (list.get(g.loopvar).getValue() == rparen) )
                { 
                    g.loopvar += 1;
                    g.counter = 0;
                }
                // if not ')' after expr
                else
                {
                    //  error
                    g.counter = 1;
                }
                    
            }
            // if it was not an id, an number, or a '('
            else
            {
                g.loopvar += 1;
                // error
                g.counter = 1;
            }
        }
    }
    
    /**
     * Parses Strings in the language for term
     * @param g Object of class Grammar
     */
    public void checkterm(Grammar g)
    {
        // if loop variable exceed the list size, it exits
        if (g.loopvar == list.size())
            return;
            
        // checks for factor
        checkfactor(g);
        
        // checks for * , / , %
        while ( (g.loopvar != list.size()) && 
                ( list.get(g.loopvar).getValue() == mul ||
                  list.get(g.loopvar).getValue() == div ||
                  list.get(g.loopvar).getValue() == mod ) )
        {
            // increments loop varirable 
            g.loopvar += 1;
            // sets expression to not valid
            // it will be set to valid once a term is encountered, hence it
            // calls checkfactor(g) which checks for factor
            g.counter = 1;
            
            // checks for factor
            checkfactor(g);
        }
    } 
    
    /**
     * Parses Strings in the language for expression
     * @param g Object of class Grammar
     */
    public void checkexpr(Grammar g)
    {
        // if loop variable exceed the list size, it exits
        if (g.loopvar == list.size())
            return;
    
        // parse term
        checkterm(g);
        
        // checks for + , -
        while ( ( g.loopvar != list.size()) &&    
                ( list.get(g.loopvar).getValue() == plus ||
                  list.get(g.loopvar).getValue() == min ) )
        {
            // increments loopvar
            g.loopvar += 1;
            // sets expression to not valid
            // it will be set to valid once a term is encountered, hence it
            // calls checkterm(g) which checks for term
            g.counter = 1;
            
            // checks for term
            checkterm(g);
        }
        
        if(g.loopvar != list.size())
            g.counter = 1;
        
    }

    /**
     * Takes in object of Grammar so that 'counter' and 'loopvar' can be changed
     * @param g Object of class Grammar
     * @return int 1 for invalid expression and 0 for valid expression
     */
    public int check_validity(Grammar g)
    {
        checkexpr(g);
        return g.counter;
    }

}
