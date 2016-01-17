package net.mrcullen.urlscraper;

public class LinkInfo {
	
	protected String linkText;
	
	protected PageInfo targetPage;
	protected PageInfo sourcePage;
	
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
