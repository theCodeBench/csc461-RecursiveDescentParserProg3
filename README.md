CSC 461 Programming Languages - Fall 2017

Recursive Descent Parser (Java)

Recursive descent parsing is often used in compilers to determine whether the
input source is syntactically correct. Recurseive descent parsing consists of writing a subprogram (possible recursive) for each non terminal element of the language. An input token is parsed by calling the appropriate subprogram, which 
decides if it is a valid language element. 

The program implements recursive descent parsing. This program reads each line of 
input until a blank line is entered, and tells the user whether it is a valid 
expression or not. 
