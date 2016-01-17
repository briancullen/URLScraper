package net.mrcullen.urlscraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SiteInfo implements PageInfoFactory {
	protected ConcurrentHashMap<URL, PageInfo> pages = new ConcurrentHashMap<URL, PageInfo>();
	protected transient AtomicInteger pagesToCheck = new AtomicInteger ();
	
	
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
		pagesToCheck.set(0);
		pages.clear();
		
		PageInfo page = createPageLinks(startURL.toExternalForm());
		if (page == null) {
			System.err.println("Unable to process the start page.");
			return;
		}
		
		while (pagesToCheck.get() != 0) {
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
	
	public PageInfo createPageLinks (String pageURLText) {
		PageInfo page = null;
				
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
						page = new PageInfo(pageURL, this);
						pages.put(pageURL, page);
						pagesToCheck.getAndIncrement();
						
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
		ArrayList<PageInfo> table = new ArrayList<PageInfo>(pages.values());
		Collections.sort(table);

		for (PageInfo page : table) {
			System.out.println(page.getPageURL() + " " + page.getLinkCount());
		}
	}
	
	private class PageProcessing implements Runnable {
		protected PageInfo page;
		
		public PageProcessing (PageInfo page) {
			this.page = page;
		}

		@Override
		public void run() {
			page.run();
			if (pagesToCheck.decrementAndGet() == 0) {
				synchronized (pagesToCheck) {
					pagesToCheck.notify();
				}
			}
		}
		
		
	}
}
