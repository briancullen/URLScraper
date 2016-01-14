# URLScraper
Simple program to count the number of links to pages in a website and rank them by the number of links. Created as part of an experiment with a class with a class when we were discussing ways in which google ranked pages.

To run simply execute the provided python file with a Python3 interpreter. The program will ask for the base URL which should point to the home page for the website that you want to check. For example to check Google you would use http://www.google.com/ as the baseURL. 

At the moment the program outputs a line for each page it checks (along with any errors) so for large sites there will be a lot of output. At the end a sorted set of tuples will be printed out showing how many links there were to each page in the website. The page with the largest number of links is shown at the bottom of the list.

## Warning
Please be aware that if you run this program it will request every webpage from the site you specify so use with caution. Other people might not like you repeated hammering their servers with this number of requests.

## Educational Links

 * Can be used to talk about the basics of the Google page rank algorithm (i.e. ranking pages by links).
 * Can be used to demonstrate the differences between sets, arrays and tuples in Python.
 * Can be used to demonstrate regular expressions and searching text files (that is how the links are extracted).
 * Can be used to show how network resources can be accessed via Python.
