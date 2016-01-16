package net.mrcullen.urlscraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class URLScraper {
	
	public static void main(String[] args) {
		String inputURL = null;
		if (args.length != 1) {
			BufferedReader reader = new BufferedReader
					(new InputStreamReader (System.in));
			
			try {
				while (inputURL == null ||  inputURL.length() == 0) {
					System.out.print("Please enter the URL: ");
					inputURL = reader.readLine();
				}
			}
			catch (IOException exception) {
				System.err.println("Unable to read base URL.");
				System.exit(1);
			}
		} else {
			inputURL = args[0];
		}
		
		try {
			// Assume we are talking about http connections
			if (!inputURL.matches("^https?://")) {
				inputURL = "http://" + inputURL;
			}

			// Converts the URL string to a URL object.
			URL firstURL = new URL(inputURL);
			
			SiteInfo site = new SiteInfo (firstURL);
			site.process();
			
			// ARGHHHH !! Annoying me!
			// Thread.sleep(20000);
			site.outputPageStatistics();
		}
		catch(MalformedURLException exception) {
			System.err.println("Incorrect URL specified: " + inputURL);
			System.exit(1);
		}		
	}

}
