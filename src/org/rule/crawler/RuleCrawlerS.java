/* Shimmie Image crawler - Crawls images from shimmie based sites.
 * Copyright (c) Vinay Bharadwaj (vbharadwaj@gatech.edu)
 *
 * SINGLE - THREADED VERSION 
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

 class RuleCrawlerS{
     public static void main(String [] args)throws Exception{
         int i=1;
         int picovrwrtprotect = 0;
         int num = 0;
         String searchstring = new String();
         String storepath = new String();
         Scanner in = new Scanner(System.in);

         System.out.println("Enter search string: ");
         searchstring = in.nextLine();
         System.out.println("Enter store path: ");
         storepath = in.nextLine();
         in.close();
         int n=0;
         String s;

         try{
             Document doc = Jsoup.connect("http://site.com/post/list/"+searchstring+"/"+i).get();

             Element paginator = doc.getElementById("paginator");
             Elements pglinks = paginator.select("a");


             for(Element pln : pglinks){
                 Elements pgtemp = pln.getElementsContainingText("Last");
                 for(Element t : pgtemp){
                     s = t.attr("href").toString();
                     s = s.replaceAll("[a-zA-Z]", "");

                     s = s.replaceAll("[_/]", "");
                     System.out.println(s);
                     n = Integer.parseInt(s);
                 }
                 
             }
         }
         catch(Exception e){}

         System.out.println("No. of pages is "+n);
         if(n==0){System.err.println("Sorry, the search string did not return any result!");}

         for (i=1; i<=n; i++)
             try{
                 DefaultHttpClient httpclient = new DefaultHttpClient();
                 Document doc = Jsoup.connect("http://site.com/post/list/"+searchstring+"/"+i).get();

                 Elements elts = doc.getElementsByClass("thumb");
                 Elements links = elts.select("a");

                 HttpGet httpget;
                 HttpResponse response;
                 InputStream istream;
                 File file;
                 FileOutputStream ostream;
                 String imgname = new String();
                 Elements temp;
                 
                 for(Element ln : links){
                     //System.out.println(ln.getElementsContainingText("Image Only").attr("href").toString());
                     if(ln.select("img").hasAttr("alt")){
                         imgname = ln.select("img").attr("alt");
                         imgname = imgname.replaceAll("[./]", "");
                         //imgname = imgname.replaceAll("[0-9]", "");
                         System.out.println(imgname);
                     }
 
                     temp = ln.getElementsContainingText("Image Only");
                     if(temp.hasText()){
                         //System.out.println(temp.attr("href").toString());

                         httpget = new HttpGet(temp.attr("href").toString());
                         response = httpclient.execute(httpget);

                         istream = response.getEntity().getContent();
                         file = new File("f:/images/"+storepath+"/" + imgname + picovrwrtprotect +".jpg");
                         picovrwrtprotect++;
                         ostream = new FileOutputStream(file, true);

                         org.apache.commons.io.IOUtils.copy(istream,ostream);
                         ostream.flush();
                         ostream.close();
                         istream.close();
                         num++;
                     }
                 }
             }
         catch(Exception e){}
         System.out.println("Got "+num+" images!");
     }
 }
	
	
