package net.mrcullen.urlscraper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement( name="link" )
@XmlType ( propOrder={} )
@XmlAccessorType ( XmlAccessType.NONE )
public class LinkInfo {
	
	@XmlAttribute (name = "text", required = true)
	private String linkText;

	private PageInfo targetPage;
	
	private PageInfo sourcePage;
	
	private LinkInfo () {}
	
	public LinkInfo (String linkText, PageInfo sourcePage, PageInfo targetPage) {
		if (linkText == null || sourcePage == null || targetPage == null) {
			throw new NullPointerException ("Link text and pages must be specified");
		}
		
		this.linkText = linkText;
		this.sourcePage = sourcePage;
		this.targetPage = targetPage;
	}
	
	public String getLinkText () { return linkText; }
	public PageInfo getSourcePage () { return sourcePage; }
	public PageInfo getTargetPage () { return targetPage; }

}
