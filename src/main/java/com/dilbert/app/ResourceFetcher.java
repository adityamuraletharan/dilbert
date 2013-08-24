package com.dilbert.app;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class ResourceFetcher {
   
	private static final Logger log = Logger.getLogger(ResourceFetcher.class);
    private String url;
    private int timeout = 10000;
    private String userAgent = "Mozilla";
    private Connection connection;
    
    public ResourceFetcher() { }
    public ResourceFetcher(String url) {
	this.url = url;
    }

    private void connect() {
    URL Url;
	try {
		Url = new URL(url);
	    log.info("Resource size : " + Url.openConnection().getContentLength());
	} catch (MalformedURLException e) {
		log.error("Malformed URL Exception : ", e);
	}
	catch (IOException e) {
		log.error("IO Exception : ", e);
	}

	connection = Jsoup.connect(url).timeout(timeout).userAgent(userAgent).ignoreContentType(true);
    }
    
    public Response fetchResource() {
	connect();
	Response ret = null;
	try { 
	    ret = connection.execute();
	} catch(IOException e) {
	    System.out.println("IO Exception : " + e);
	}
	return ret;
    }
    
    
    


}