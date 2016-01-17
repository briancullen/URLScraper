package net.mrcullen.urlscraper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

public class PageInfo implements Runnable, Comparable<PageInfo> {
	protected HashSet<PageInfo> links = new HashSet<PageInfo>();
	protected PageInfoFactory factory = null;
	
	protected URL pageURL = null;
	protected String pageName = null;
	protected int linkCount = 0;
	
	public PageInfo (URL url, PageInfoFactory factory) {
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
		Pattern regex = Pattern.compile("text/html");
		
		URLConnection connection = pageURL.openConnection();
		if (regex.matcher(connection.getContentType()).find()) {
			Document doc = Jsoup.parse(connection.getInputStream(),
					connection.getContentEncoding(), pageURL.toExternalForm());
			pageName = doc.title();
			
			Elements elements = doc.getElementsByTag("a");
			for (Element element : elements) {
				PageInfo newPage = factory.createPageLinks(element.attr("href"));
				if (newPage != null) {
					links.add(newPage);
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			processPage();
		}
		catch (IOException exception) {
			System.err.println("*** Unable to process: " + exception.getMessage());
		}
	}

	@Override
	public int compareTo(PageInfo other) {
		return linkCount - other.linkCount;
	}
	
}
