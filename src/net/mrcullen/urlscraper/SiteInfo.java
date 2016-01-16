package net.mrcullen.urlscraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SiteInfo implements PageLinksFactory {
	public Hashtable<URL, PageLinks> pages = new Hashtable<URL, PageLinks>();
	
	protected URL baseURL = null;
	protected URL startURL = null;
	
	protected transient ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public SiteInfo (URL startURL)
			throws MalformedURLException {
		String relativeURL = startURL.getPath();
		if (relativeURL.matches(".+\\.(html?|php)$")) {
			String[] components = relativeURL.split("/");
			
			relativeURL = "/";
			for (int index = 1; index < components.length-1; index++) {
				relativeURL += components[index] + "/";
			}
		}
		
		this.startURL = startURL;
		baseURL = new URL(startURL, relativeURL);
	}
	
	public void process () {
		PageLinks page = createPageLinks(startURL.toExternalForm());
		if (page == null) {
			System.err.println("Unable to process the start page.");
			return;
		}
	}
	
	public PageLinks createPageLinks (String pageURLText) {
		PageLinks page = null;
		
		try {
			URL pageURL = new URL (baseURL, pageURLText);
			
			if (pageURL.toString().startsWith(baseURL.toString())) {				
				
				synchronized (pages) {
					page = pages.get(pageURL); 
					if (page == null)
					{
						System.out.println(pageURL.toString());
						page = new PageLinks(pageURL, this);
						pages.put(pageURL, page);
						threadPool.execute(page);
					}
				}
				
				page.incrementLinkCount();
			}
		}
		catch (MalformedURLException exception) { }
		return page;
	}
}
