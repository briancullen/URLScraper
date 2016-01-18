package net.mrcullen.urlscraper;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

@XmlRootElement( name="page" )
@XmlType ( propOrder={"pageName", "linkCount", "incomingLinks", "outgoingLinks" } )
@XmlAccessorType ( XmlAccessType.NONE )
public class PageInfo implements Runnable, Comparable<PageInfo> {
	
	@XmlElement ( name = "outgoing", required = true )
	private HashSet<LinkInfo> outgoingLinks = new HashSet<LinkInfo>();
	
	@XmlElement ( name = "incoming", required = true )
	private HashSet<LinkInfo> incomingLinks = new HashSet<LinkInfo>();
	private PageInfoFactory factory = null;
	
	@XmlElement ( name = "name", required = true )
	private String pageName = null;
	
	@XmlAttribute ( name = "location", required = true )
	private URL pageURL = null;
	
	@XmlElement ( name = "links", required = true )
	private int linkCount = 0;
	
	protected PageInfo () { }
	
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
	
	public Iterator<LinkInfo> getIncomingLinks () { return incomingLinks.iterator(); }
	public Iterator<LinkInfo> getOutgoingLinks () { return outgoingLinks.iterator(); }
	
	public boolean addIncomingLink (String linkText, PageInfo sourcePage) {
		return incomingLinks.add(new LinkInfo(linkText, sourcePage, this));
	}
	
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
					outgoingLinks.add(new LinkInfo (element.text(), this, newPage));
					newPage.addIncomingLink(element.text(), this);
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
