package net.mrcullen.urlscraper;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

public class PageLinks implements Runnable {
	protected HashSet<PageLinks> links = new HashSet<PageLinks>();
	protected PageLinksFactory factory = null;
	
	protected URL pageURL = null;
	protected String pageName = null;
	protected int linkCount = 0;
	
	public PageLinks (URL url, PageLinksFactory factory) {
		if (url == null) {
			throw new NullPointerException("Null URL provided.");
		}

		this.factory = factory;
		pageURL = url;
	}
	
	public URL getPageURL () { return pageURL; }
	public String getPageName () { return pageName; }
	public int getLinkCount () { return linkCount; }
	public synchronized void incrementLinkCount () { linkCount++; }
	
	public void processPage ()
			throws IOException {
		Document doc = Jsoup.connect(pageURL.toExternalForm()).get();
		pageName = doc.title();
		
		Elements elements = doc.getElementsByTag("a");
		for (Element element : elements) {
			PageLinks newPage = factory.createPageLinks(element.attr("href"));
			if (newPage != null) {
				links.add(newPage);
			}
		}
	}

	@Override
	public void run() {
		try {
			processPage();
		}
		catch (IOException exception) {
			System.out.println("*** Unable to process: " + pageURL.toString() + exception.getMessage());
		}
	}
	
}
