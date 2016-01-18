package net.mrcullen.urlscraper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;

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
			
			site.outputPageStatistics();
			System.out.println("********************************");
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SiteInfo.class, PageInfo.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(site, ostream);

			System.out.println(ostream.toString());
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//			SiteInfo info = (SiteInfo)jaxbUnmarshaller.unmarshal(new ByteArrayInputStream (ostream.toByteArray()));
			System.out.println("********************************");

			SchemaOutputResolver outputResolver = new TestSchemaOutputResolver ();
			jaxbContext.generateSchema(outputResolver);
			

		}
		catch(MalformedURLException exception) {
			System.err.println("Incorrect URL specified: " + inputURL);
			System.exit(1);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
