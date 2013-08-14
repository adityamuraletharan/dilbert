package com.dilbert.app;


import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
	try {
	    //read url from properties
	    //TODO: Include functionality to iterate over all dates
	    String outDir = "/home/aditya/DilbertStrips/";
	    Document doc = Jsoup.connect("http://dilbert.com/strips/comic/2013-08-10/").timeout(10000).userAgent("Mozilla").get();
	    String title = doc.title();
	    System.out.println("Title: " + title);
	    Elements children = doc.select("img[src^=/dyn]");
	    Elements img = children.select("img[src*=zoom]");
	    assert(img.size() == 1);
	    for(Element e : img) {
		String imgUrl = e.absUrl("src");
		Connection.Response response = Jsoup.connect(imgUrl).ignoreContentType(true).timeout(10000).execute();
		FileOutputStream out = new FileOutputStream(new File(outDir + "test.gif"));
		out.write(response.bodyAsBytes());
		System.out.println(imgUrl);
		}
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
}
