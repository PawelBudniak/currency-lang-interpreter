# currency-lang-interpreter
Project for a compilation techniques class.
It includes a formal definition of my own custom programming language with built-in support for processing currencies,
as well as an interpreter for the language. The syntax is based on the C language.  
Example code snippet:
```
currency franks = 0.53chp; # declare and initialize variable
currency zlotys = 2.33pln;
currency my_franks = franks + [chp]zlotys; # cast pln currency to chp and perform addition
```
The interpreter uses user-defined exchange rates when casting one currency type to another.
They can be defined in a json file in a format like this (the default location is data/exchangeRates.json):
```
{
  "pln":
  {
    "gbp": "0.2",
    "usd": "0.33"
  },
  "gbp":
  {
    "pln": "5.0",
    "usd": "1.33"
  },
  "usd":
  {
    "pln": "3.0",
    "gbp": "0.7"
  }
}
```
Exchange rates can also be defined programatically:
```
exchange from gbp to pln 4.32;
```
Users can define their own functions, which can be recursive:
```
number fib (number n){
  if (n==0 || n == 1){
    return 1;
   }
  return fib(n-2) + fib(n-1);
}
```

### Formal grammar of the language in the EBNF format:

```
# Whitespace inbetween these constructs are ignored
program = { function_def };
function_def = fun_type_specifier, identifier, "(", arg_def_list, ")", block;
fun_type_specifier = type_specifier | "void";
block = "{" {statement } "}";
statement = assignOrFunCall | exchange_declaration | if_statement | loop |
return_statement;
return_statement = “return”, rvalue;
assignOrFunCall = (identifier, ( rest_of_funcall| rest_of_assignment)) |
type_and_id, rest_of_assignment
rest_of_assignment = "=", rvalue, ";";
rest_of_funcall = "(" arg_call_list ")", ";";
type_specifier = "bool" | "currency" | "number" | "string";
arg_def_list = [ type_and_id {",", type_and_id} ];
arg_call_list = [ rvalue {",", rvalue} ];
exchange_declaration = "exchange from", currency_type, "to", currency_type,
rvalue, ";";
if_statement = "if", "(", bool_expression, ")", block;
loop = "while", "(", bool_expression, ")", block;
lvalue = type_and_id | identifier;
rvalue = bool_expression;
arithmetic_expression = [unary_op], term , { additive_operator , term};
term = factor , { multiplicative_operator , factor};
factor = [unary_op], simple_value | ( "(" , bool_expression, ")" );
bool_expression = bool_term, {"||", bool_term};
bool_term = bool_factor, {"&&", bool_factor};
bool_factor = [unary_op],
comparison | arithmetic_expression
type_and_id = type_specifier, identifier;
comparison = simple_value, logic_operator, simple_value;
simple_value = identifier | literal | function_call;
unary_op = "!" | "-" | cast_op;
cast_op = "[", currency_type, "]";
literal = str_literal | bool_literal | number_literal | currency_literal;
currency_literal = number_literal, currency_type;  

# Whitespaces inbetween these constructs are not ignored

number_literal = decimal | integer;
bool_literal = "True" | "False"
str_literal = "'", [^’]* , "'";
identifier = letter, {alnum};
alnum = letter | digit ;
letter = #'[A-Za-z]';
decimal = integer , "." , digit , { digit };
integer = "0" | (non_zero_digit, {digit});
digit = "0" | non_zero_digit;
non_zero_digit = #'[1-9]*';
additive_operator = "+" | "-";
multiplicative_operator = "*" | "/";
logic_operator = "<" | ">" | "<=" | ">=" | "==" | "!=";
currency_type = #'[a-z]';
```
