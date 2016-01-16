# URLScraper
Simple programs to count the number of links to pages in a website and rank the webpages by the number of links. Created as part of an experiment with a class with a class when we were discussing ways in which google ranked pages. There are now three different versions, two in Python and one in Java.

All versions of the program will ask for the base URL which should point to the home page for the website that you want to check. For example to check Google you would use http://www.google.com/ as the base URL.

At the moment some versions of the program outputs a line for each page it checks (along with any errors) so for large sites there will be a lot of output. At the end a sorted set of tuples will be printed out showing how many links there were to each page in the website. The page with the largest number of links is shown at the bottom of the list.

## Java
The eclipse project files are part of the repository if you want to run it that way. Otherwise the following commands executed from the root directory for the project will suffice.

```
mkdir bin
javac -classpath "lib/jsoup-1.8.3.jar" -d bin src/net/mrcullen/urlscraper/*
java -classpath "bin:lib/jsoup-1.8.3.jar" net.mrcullen.urlscraper.URLScraper
```

## Python
There are now two versions of the python program, one threaded and one not. To run the program simply execute the provided python file (in the `python_src` folder) with a Python3 interpreter.

## Warning
Please be aware that if you run this program it will request every webpage from the site you specify so use with caution. Other people might not like you repeated hammering their servers with this number of requests.

## Educational Links

 * Can be used to talk about the basics of the Google page rank algorithm (i.e. ranking pages by links).
 * Can be used to demonstrate the differences between sets, arrays and tuples in Python.
 * Can be used to demonstrate regular expressions and searching text files (that is how the links are extracted).
 * Can be used to show how network resources can be accessed via Python.
 * Can be used to show the advantages and issues involved with Threading.
