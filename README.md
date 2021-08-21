# currency-lang-interpreter
Project for a compilation techniques class.
It includes a formal definition of my own custom programming language with built-in support for processing currencies,
as well as an interpreter for the language. The syntax is based on the C language.  
Example code snippet:
```
currency franks = 0.53chp; # declare ant initialize variable
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
  "usd": {
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
