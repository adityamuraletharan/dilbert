package com.dilbert.app;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ResourceScraper {
	
	private static final Logger log = Logger.getLogger(ResourceScraper.class); 
    private String url;
    private List<String> selectors;
    private Document doc;
    private Connection connection;
    private int timeout = 10000;
    private String userAgent = "Mozilla";
    
    public ResourceScraper() { }
    public ResourceScraper(String url) { 
	this.url = url; 
    }
    
    public void setSelectors(List<String> selectors) {
	this.selectors = selectors;
    }

    public List<String> getSelectors() {
	return selectors;
    }

    private void connect() {
	connection = Jsoup.connect(url).timeout(timeout).userAgent(userAgent);
    }
    
    private void parse() {
	try {
	    doc = connection.get();
	} catch(IOException e) {
	    System.out.println("IO Exception caught: " + e);
	}
    }

    public Elements scrape() {
	connect();
	parse();
	
	Elements ret = doc.select("*");
	for(String sel : selectors) {
	    ret = ret.select(sel);
	}
	return ret;
    }
}