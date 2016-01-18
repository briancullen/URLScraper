package net.mrcullen.urlscraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement( name="site" )
@XmlType ( propOrder={"pages"} )
@XmlAccessorType ( XmlAccessType.NONE )
public class SiteInfo implements PageInfoFactory {
	
	@XmlJavaTypeAdapter (PageMapAdapter.class)
	@XmlElement ( required = true )
	private ConcurrentHashMap<URL, PageInfo> pages = new ConcurrentHashMap<URL, PageInfo>();
	
	private transient AtomicInteger pagesToCheck = new AtomicInteger ();
	
	private URL baseURL = null;
	private URL startURL = null;
	
	@XmlAttribute ( name = "location", required = true )
	public URL getStartURL () {
		return startURL;
	}
	
	protected void setStartURL (URL startURL) throws MalformedURLException {
		String relativeURL = startURL.getPath();
		if (relativeURL.matches(".+\\.(html?|php)$")) {
			String[] components = relativeURL.split("/");
			
			relativeURL = "/";
			for (int index = 1; index < components.length-1; index++) {
				relativeURL += components[index] + "/";
			}
		}
		else if (!relativeURL.endsWith("/")) {
			relativeURL += "/";
			startURL = new URL (startURL, relativeURL);
		}
		
		baseURL = new URL(startURL, relativeURL);		
		this.startURL = startURL;
	}
	
	public Iterator<PageInfo> getSitePages() { return pages.values().iterator(); }
	
	protected transient ExecutorService threadPool = Executors.newCachedThreadPool();
	
	protected SiteInfo () { }
	
	public SiteInfo (URL startURL)
			throws MalformedURLException {
		setStartURL(startURL);
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
