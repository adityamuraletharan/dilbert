package com.dilbert.app;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.dilbert.app.AppConstants;
/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger log = Logger.getLogger(App.class);
    
    private static String outputDirectory;
    private static List<String> selectors = new ArrayList<String>();
    private static String urlHeader;
    private static DateFormat dateFormat;
    private static DateFormat fileNameFormat;
    private static String startDay;
    private static ResourceScraper resourceScraper;
    private static ResourceFetcher resourceFetcher;
    
    public static void main( String[] args )
    {
	//TODO: UT, Flow Test, AspectJ, Multithreading;
    
	/*
	 * Generate URL based on date
	 * Send the URL to ResourceScraper and get the image URL based on select rules
	 * Fetch the image using ResourceFetcher
	 * Build the directory structure
	 * Save the image
	 */

	Properties appConfig = new Properties();
	try {
		appConfig.load(new FileInputStream("/home/aditya/workspace/dilbert/src/main/resources/application.properties"));
		log.info("App Config initialized");
		outputDirectory = appConfig.getProperty(AppConstants.OUTPUT_DIRECTORY);
		urlHeader = appConfig.getProperty(AppConstants.URL_HEADER);
		dateFormat = new SimpleDateFormat(appConfig.getProperty(AppConstants.DATE_FORMAT));
		fileNameFormat = new SimpleDateFormat(appConfig.getProperty(AppConstants.FILE_NAME_FORMAT));
		startDay = appConfig.getProperty(AppConstants.START_DAY);
		
		
		String conf = appConfig.getProperty(AppConstants.SELECTORS);
		String[] sels = conf.split(",");
		log.debug(sels.length);
		for(String sel : sels) {
			selectors.add(sel);
		}
		
		log.info("Output directory: " + outputDirectory);
		log.info("URL Header: " + urlHeader);
		log.info("Date format: " + appConfig.getProperty(AppConstants.DATE_FORMAT));
		log.info("Image name format: " + appConfig.getProperty(AppConstants.FILE_NAME_FORMAT));
		log.info("Start Day: " + startDay);
		for(String sel : selectors) {
			log.info("Selection regex while scraping: " + sel);
		}
		
	} catch (FileNotFoundException e1) {
		log.error("File Not Found Exception: ", e1);
	} catch (IOException e1) {
		log.error("IO Exception: " + e1);
	}
	
	Long counter = 1L;
	
	String currentDay;
	String endDay = dateFormat.format(new Date());
	//Testing for first four years
	endDay = "1993-12-31";
	
	try {
	    Date startDate = dateFormat.parse(startDay);
	    Date currentDate;
	    Date endDate = dateFormat.parse(endDay);

	    Calendar currentCal = Calendar.getInstance();
	    currentCal.setTime(startDate);
	    Calendar endCal = Calendar.getInstance();
	    endCal.setTime(endDate);
	    while(currentCal.compareTo(endCal) <= 0) {
		//Convert calendar -> date -> string
		currentDate = currentCal.getTime();
		currentDay = dateFormat.format(currentDate);
		log.info("Scraping Image Url for " + currentDay + " from " + urlHeader + currentDay);
		//Passing URL to ResourceScraper for acquiring the Document with the required image source
		Long start = System.currentTimeMillis();
		resourceScraper = new ResourceScraper(urlHeader + currentDay);
		resourceScraper.setSelectors(selectors);
		Elements elmnts = resourceScraper.scrape();
		log.info("Scraped Image Url in " + (System.currentTimeMillis() - start) + " ms");
		
		for(Element elmnt : elmnts) {
		    String imgUrl = elmnt.absUrl("src");
		    log.info("Image URL : " + imgUrl);
		    //Passing image URL to ResourceFetcher for fetching the image
		    start = System.currentTimeMillis();
		    resourceFetcher = new ResourceFetcher(imgUrl);
		    Connection.Response res = resourceFetcher.fetchResource();
		  
		    log.info("Fetched image in " + (System.currentTimeMillis() - start) + " ms");
		    log.info("Response status for Image Fetch : " + res.statusMessage());
		    
		    try {
			
		    	String outDir = buildDirectoryStructure(currentDate);
		    	log.info("Saving into directory : " + outDir);
		    	File file = new File(outDir);
		    	if(!file.exists())
		    		file.mkdirs();
		    	String fileName = buildFileName(currentDate);
		    	file = new File(outDir + fileName);
		    	log.info("Saving image to file : " + outDir + fileName);
		    	FileOutputStream out = new FileOutputStream(file);
		    	out.write(res.bodyAsBytes());
		    	out.close();
		    	log.info("Wrote Image to file : " + outDir + fileName);
		    } catch(FileNotFoundException e) {
			log.error("File Not Found Exception : " + e);
		    }
		    catch(IOException e) {
			log.error("IO Exception : " + e);
		    }
		}
	    
		counter++;
		currentCal.add(Calendar.DATE, 1);
	    }
	} catch(ParseException e) {
	    System.out.println("Parse Exception : " + e);
	}
	
    }


    //Extracts the year out of the parameter and returns directory path
    private static String buildDirectoryStructure(Date date) {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	return outputDirectory + "/" + String.valueOf(cal.get(Calendar.YEAR)) + "/";
	
    }

    private static String buildFileName(Date date) {
	return fileNameFormat.format(date);
    }
}
