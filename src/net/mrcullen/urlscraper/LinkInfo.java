package net.mrcullen.urlscraper;

public class LinkInfo {
	
	protected String linkText;
	protected PageInfo sourcePage;
	
	public LinkInfo (String linkText, PageInfo sourcePage) {
		if (linkText == null || sourcePage == null) {
			throw new NullPointerException ("Link text and source page must be specified");
		}
		
		this.linkText = linkText;
		this.sourcePage = sourcePage;
	}
	
	public String getLinkText () { return linkText; }
	public PageInfo getSourcePage () { return sourcePage; }

}
