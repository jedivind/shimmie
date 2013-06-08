/* Shimmie Crawler - Crawles all web images in all the pages on the search list from a shimmie based site for a given search string.
 * Usage: Input search string - essentially one of the tags on the site and a path to store the files. 
 * Copyright Vinay Bharadwaj (vind.1989@gatech.edu)

 * MULTI - THREADED VERSION 
 */
package org.rule.crawler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Object;
import java.lang.Math;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


class RuleCrawler implements Runnable {
	public boolean finished = false; // Thread finished
	public Thread runner; // Thread pointer - Reference to thread object
	private int i, n; // Start & End indices
	private String searchstring, storepath;
	private String basepath = "F:/images";
	private int picovrwrtprotect = 0;
	private int num = 0;

	public void run() {
		crawl();
		finished = true;
	}

	public RuleCrawler(int beginIndex, int endIndex, String searchString,
			String storePath) {
		i = beginIndex;
		n = endIndex;
		searchstring = searchString;
		storepath = storePath;
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void crawl() {
		for (int j = i; j <= n; j++)
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				Document doc = Jsoup.connect(
						"http://site.com/post/list/" + searchstring + "/" + i)
						.get(); // Replace site.com with relevant site.

				Elements elts = doc.getElementsByClass("thumb");
				Elements links = elts.select("a");

				HttpGet httpget;
				HttpResponse response;
				InputStream istream;
				File file;
				FileOutputStream ostream;
				String imgname = new String();
				Elements temp;
				for (Element ln : links) {
					// System.out.println(ln.getElementsContainingText("Image Only").attr("href").toString());
					if (ln.select("img").hasAttr("alt")) {
						imgname = ln.select("img").attr("alt");
						imgname = imgname.replaceAll("[./]", "");
						// imgname = imgname.replaceAll("[0-9]", "");
						System.out.println(imgname);
					}
                    
					temp = ln.getElementsContainingText("Image Only");
					if (temp.hasText()) {
						// System.out.println(temp.attr("href").toString());

						httpget = new HttpGet(temp.attr("href").toString());
						response = httpclient.execute(httpget);

						istream = response.getEntity().getContent();
						try {
							boolean success = new File(basepath + storepath)
									.mkdir();
							file = new File(basepath + storepath + "/"
									+ imgname + picovrwrtprotect + ".jpg");
							picovrwrtprotect++;
							ostream = new FileOutputStream(file, true);

							org.apache.commons.io.IOUtils
									.copy(istream, ostream);
							ostream.flush();
							ostream.close();
							istream.close();
						} catch (Exception e) {
							System.err.println(e.toString());
						}

						num++;
					}
				}
			} catch (Exception e) {
			}
		System.out.println("Got " + num + " images!");
	}
}

