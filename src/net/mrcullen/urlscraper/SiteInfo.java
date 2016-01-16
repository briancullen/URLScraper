package net.mrcullen.urlscraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class SiteInfo implements PageLinksFactory {
	protected ConcurrentHashMap<URL, PageLinks> pages = new ConcurrentHashMap<URL, PageLinks>();
	protected transient Vector<PageLinks> pagesToCheck = new Vector<PageLinks> ();
	
	
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
		pagesToCheck.clear();
		pages.clear();
		
		PageLinks page = createPageLinks(startURL.toExternalForm());
		if (page == null) {
			System.err.println("Unable to process the start page.");
			return;
		}
		
		while (!pagesToCheck.isEmpty()) {
			try {
				synchronized (pagesToCheck) {
					pagesToCheck.wait();
				}
			}
			catch (InterruptedException exception) {}
		}
		
		threadPool.shutdown();
		while (!threadPool.isTerminated()) {
			try {
				threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			}
			catch (InterruptedException exception) {}
		}
	}
	
	public PageLinks createPageLinks (String pageURLText) {
		PageLinks page = null;
				
		if (pageURLText == null || pageURLText.length() == 0
				|| pageURLText.startsWith("#"))
			return null;
		
		try {
			URL pageURL = new URL (baseURL, pageURLText);
			
			if (pageURL.toString().startsWith(baseURL.toString())) {				
				
				synchronized (pages) {
					page = pages.get(pageURL); 
					if (page == null)
					{
						page = new PageLinks(pageURL, this);
						pages.put(pageURL, page);
						pagesToCheck.add(page);
						
						PageProcessing task = new PageProcessing(page);
						threadPool.execute(task);
					}
				}
				
				page.incrementLinkCount();
			}
		}
		catch (MalformedURLException exception) { }
		return page;
	}
	
	public void outputPageStatistics () {
		ArrayList<PageLinks> table = new ArrayList<PageLinks>(pages.values());
		Collections.sort(table);

		for (PageLinks page : table) {
			System.out.println(page.getPageURL() + " " + page.getLinkCount());
		}
	}
	
	private class PageProcessing implements Runnable {
		protected PageLinks page;
		
		public PageProcessing (PageLinks page) {
			this.page = page;
		}

		@Override
		public void run() {
			page.run();
			pagesToCheck.remove(page);
			if (pagesToCheck.isEmpty()) {
				synchronized (pagesToCheck) {
					pagesToCheck.notify();
				}
			}
		}
		
		
	}
}
