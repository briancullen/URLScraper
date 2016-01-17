package net.mrcullen.urlscraper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PageMapAdapter extends
	XmlAdapter<PageMapAdapter.AdaptedProperties, ConcurrentHashMap<URL, PageInfo>>{

	public static class AdaptedProperties {
		@XmlElement (name = "page")
		public ArrayList<PageInfo> list = new ArrayList<PageInfo> ();
		
		public AdaptedProperties () {}
		public AdaptedProperties (ArrayList<PageInfo> list) { this.list = list; }
	}
	
	@Override
	public AdaptedProperties marshal(ConcurrentHashMap<URL, PageInfo> map) throws Exception {
		ArrayList<PageInfo> list = new ArrayList<PageInfo> (map.values());		
		Collections.sort(list);
		
		return new AdaptedProperties(list);
	}

	@Override
	public ConcurrentHashMap<URL, PageInfo> unmarshal(AdaptedProperties list) throws Exception {
		ConcurrentHashMap<URL, PageInfo> pages = new ConcurrentHashMap<URL, PageInfo>();
		
		for (PageInfo page : list.list) {
			pages.put(page.getPageURL(), page);
		}
		
		return pages;
	}

}
