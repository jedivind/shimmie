//Rule 34 Crawler - Crawles all web images in all the pages on the search list from a shimmie based site for a given search string.
//Usage: Input search string - essentially one of the tags on the site and a path to store the files. 
//Copyright Vinay Bharadwaj (vind.1989@gatech.edu)
/*
 * 
 * SINGLE - THREADED VERSION - Backup
 * 
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

class rule{
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
        Document doc = Jsoup.connect("http://rule34.paheal.net/post/list/"+searchstring+"/"+i).get();
            
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
		Document doc = Jsoup.connect("http://rule34.paheal.net/post/list/"+searchstring+"/"+i).get();
				
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
        	
        	//
        	if(ln.select("img").hasAttr("alt")){
        	imgname = ln.select("img").attr("alt");
        	imgname = imgname.replaceAll("[./]", "");
        	//imgname = imgname.replaceAll("[0-9]", "");
        	System.out.println(imgname);
        	}
        	//
        	temp = ln.getElementsContainingText("Image Only");
        	if(temp.hasText()){
        	//System.out.println(temp.attr("href").toString());
        	
        	httpget = new HttpGet(temp.attr("href").toString());
    	    response = httpclient.execute(httpget);
    	    
    	    istream = response.getEntity().getContent();
    	    file = new File("f:/Track No01/Hentai - Art - Pencil/"+storepath+"/" + imgname + picovrwrtprotect +".jpg");
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
*//////////////////////////////////////////////////////////////////////////

/* MULTI - THREADED VERSION - DEBUG */
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

class rulecrawler implements Runnable{
	public boolean finished = false; //Thread finished
	public Thread runner; //Thread pointer - Reference to thread object
	private int i, n; //Start & End indices
	private String searchstring, storepath;
	private String basepath = "F:/images";
	private int picovrwrtprotect = 0;
	private int num = 0;
	
	public void run(){
		crawl();
		finished = true;
	}
	
	public rulecrawler(int beginIndex, int endIndex, String searchString, String storePath){
		i = beginIndex;
		n = endIndex;
		searchstring = searchString;
		storepath = storePath;
		if(runner==null){
			runner = new Thread(this);
			runner.start();
		}
	}
	
	public void crawl(){
		for (int j=i; j<=n; j++)
			try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			Document doc = Jsoup.connect("http://site.com/post/list/"+searchstring+"/"+i).get(); //Replace site.com with relevant site.
					
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
	        	
	        	//
	        	if(ln.select("img").hasAttr("alt")){
	        	imgname = ln.select("img").attr("alt");
	        	imgname = imgname.replaceAll("[./]", "");
	        	//imgname = imgname.replaceAll("[0-9]", "");
	        	System.out.println(imgname);
	        	}
	        	//
	        	temp = ln.getElementsContainingText("Image Only");
	        	if(temp.hasText()){
	        	//System.out.println(temp.attr("href").toString());
	        	
	        	httpget = new HttpGet(temp.attr("href").toString());
	    	    response = httpclient.execute(httpget);
	    	    
	    	    istream = response.getEntity().getContent();
	    	    try{
	    	    boolean success = new File(basepath+storepath).mkdir();	
	    	    file = new File(basepath + storepath +"/" + imgname + picovrwrtprotect +".jpg");
	    	    picovrwrtprotect++;
	    	    ostream = new FileOutputStream(file, true);
	    	 
	    	    org.apache.commons.io.IOUtils.copy(istream,ostream);
	    	    ostream.flush();
	    	    ostream.close();
	    	    istream.close();
	    	    }
	    	    catch(Exception e){System.err.println(e.toString());}
	            
	    	    num++;
	        	}
	        }
			}
	        catch(Exception e){}
		    System.out.println("Got "+num+" images!");
	}
}

class rule{
	private int i=1;
	private String searchstring;
	private String storepath;
	private int numThreads; //Unused as of now.
	private int n=0;
	private rulecrawler crawlers [];
	
	private static String taglist = "./0123456789:@[abcdefghijklmnopqrstuvwxyzÆ";
	private static char tagchar='\0';
	private static boolean tagcharspecified = false;
	private boolean searchfailed=false;
	
	public rule(){}
	
	public rule(String ss, String sp){
		this.searchstring = ss;
		this.storepath = sp;
	}
	
	//This Constructor is unused as of now. User can declared number of parallel threads to run using this. For later.
	public rule(int nthreads, String ss, String sp){
		this.numThreads = nthreads;
		this.searchstring = ss;
		this.storepath = sp;
	}
	
	public void crawl()throws Exception{
		String s;
		
		int partition=0, mod = 0; //Unused as of now. Can be used to divide pages among threads. For later.
		try{
	        Document doc = Jsoup.connect("http://site.com/post/list/"+searchstring+"/"+i).get();
	            
			Element paginator = doc.getElementById("paginator");
			Elements pglinks = paginator.select("a");
			
			
			for(Element pln : pglinks){//Each page has a link "Last" that links to the last page. Get the last page number and crawl from first page till the last.
				Elements pgtemp = pln.getElementsContainingText("Last");
				for(Element t : pgtemp){
				s = t.attr("href").toString();
				s = s.replaceAll("[a-zA-Z]", "");
				
				s = s.replaceAll("[_/]", "");
				System.out.println(s);
				n = Integer.parseInt(s);
				}
			}	
				
			if(n==0){ //Edge case. If there's only one page, The link "Last" won't be displayed on the page. In that case, just crawl that single page. If no results returned, 404 Exception is correctly displayed & the program terminates. 
				for(Element pln2 : pglinks){
					Elements pgtemp2 = pln2.getElementsContainingText("1");
					for(Element t : pgtemp2){
					s = t.attr("href").toString();
					s = s.replaceAll("[a-zA-Z]", "");
					
					s = s.replaceAll("[_/]", "");
					System.out.println(s);
					n = Integer.parseInt(s);
					}
				
			  }
				
			}
			
			 }
		
	        catch(Exception e){System.err.println(e.toString());}
	              
	        System.out.println("No. of pages is "+n);
	        System.out.println("http://rule34.paheal.net/post/list/"+searchstring+"/"+i);
	        if(n==0){searchfailed = true;
	        System.err.println("Sorry, the search string did not return any result!");
	        //throw new Exception();
	        }
	        
	        
	        //Unused. Partition pages among threads. Experimenting..
	        //partition = (int) Math.floor(numThreads/n);
	        //if( n-(partition * numThreads) > 0 ){ mod = n-(partition * numThreads);}
	        
	        if(n!=0){
	        try{
	            crawlers = new rulecrawler[n];
	        for(int j=0; j<n; j++)
	        	crawlers[j] = new rulecrawler(j+1 , j+1, searchstring, storepath);
	        	
	        for(int j=0; j<n; j++)
	        	crawlers[j].runner.join();
	    	}
	        catch(Exception e){System.err.println(e.toString());}
	        }
	      	       
	}
	
	/*void join(rulecrawler crawler){ //Unnecessary. Used inbuilt join function for efficiency.
		/*boolean joined=false;
		int count=0;
		while(!joined){
			for(int i=0; i<n; i++){
				if(crawlers[i].finished){
					count++;
				}				
			}
			if(count == n) joined=true;
			if(count != n) Thread.currentThread().yield();
		}
	}*/
	
	void tagcrawl() throws Exception{
		String basepage = "http://site.com/tags/alphabetic?show_all=on&starts_with=";
		
		int index=0;
		
		if(!tagcharspecified){
			for(int i=index; i<taglist.length(); i++){
			try{
				Document doc = Jsoup.connect(basepage + taglist.charAt(index)).get();
				Element paginator = doc.getElementById("Tagsmain");
				Elements pglinks = paginator.select("a");
				System.out.println("Tags for character" + taglist.charAt(index) +"are: ");
				for(Element pln : pglinks){
					System.out.println(pln.text());
					}
			}
		
			catch(Exception e){System.err.println(e.toString());}
			}
			
		}
		
		else{
			try{
				Document doc = Jsoup.connect(basepage + tagchar).get();
				Element paginator = doc.getElementById("Tagsmain");
				Elements pglinks = paginator.select("a");
				System.out.println("Tags for character" + tagchar +"are: ");
				for(Element pln : pglinks){
					System.out.println(pln.text());
					}
				}
			catch(Exception e){System.err.println(e.toString());}
		}
		
	}  
	
	
	public static void main(String [] args)throws Exception{
		Scanner in = new Scanner(System.in);
		int thr; //Unused. Can be used to specify number of threads by the user.
		String sstr=null, spath=null, temp=null;
		char choice='\0';
		boolean crawl=true, tagcrawl=false;
		while(crawl==true){
			System.out.println("Would you like to list tags all tags (A) or tags for specific character (S) or not (N)?");
			temp = in.nextLine();
			if(temp.toLowerCase().equals("a")){tagcharspecified = false; tagcrawl = true;}
			else if (temp.toLowerCase().equals("s")){System.out.println("Enter tag character: ");
			
			temp = in.nextLine();
			temp = temp.toLowerCase();
			tagchar = temp.charAt(0);
			tagcrawl = true;
			tagcharspecified = true;
				}
			else if (temp.toLowerCase().equals("n")){tagcrawl = false;}
			else continue;
			
			if(tagcrawl){
				rule tagcrawler = new rule();
				tagcrawler.tagcrawl();
			}
			
			while(sstr==null){
				System.out.println("Enter search string: ");
				sstr = in.nextLine();		
				if(sstr.charAt(0) == '\n') sstr = null;
			}
		
			while(spath==null){
				System.out.println("Enter store path: ");
				spath = in.nextLine();
			}
        //Unused - Take num_threads from user.
        //System.out.println("Enter number of parallel threads for crawling: ");
		//thr = Integer.parseInt(in.nextLine());
        		
        rule crawler = new rule(sstr, spath);
		crawler.crawl();
		//crawler.join();	//Unnecessary. Used inbuilt join to synchronize for efficiency.
		System.out.println("Do you want to start a new crawl? (y/n)");
		temp = in.nextLine();
		choice = temp.charAt(0);
		if(choice=='n'){crawl = false;}
		crawl=false;
		}
		in.close();
	}
}
