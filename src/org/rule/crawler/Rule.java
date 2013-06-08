package org.rule.crawler;

import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Rule {
	private int i = 1;
	private String searchstring;
	private String storepath;
	private int numThreads; // Unused as of now.
	private int n = 0;
	private RuleCrawler crawlers[];

	private static String taglist = "./0123456789:@[abcdefghijklmnopqrstuvwxyzÆ";
	private static char tagchar = '\0';
	private static boolean tagcharspecified = false;
	private boolean searchfailed = false;

	public Rule() {
	}

	public Rule(String ss, String sp) {
		this.searchstring = ss;
		this.storepath = sp;
	}

	// This Constructor is unused as of now. User can declared number of
	// parallel threads to run using this. For later.
	public Rule(int nthreads, String ss, String sp) {
		this.numThreads = nthreads;
		this.searchstring = ss;
		this.storepath = sp;
	}

	public void crawl() throws Exception {
		String s;

		int partition = 0, mod = 0; // Unused as of now. Can be used to divide
									// pages among threads. For later.
		try {
			Document doc = Jsoup.connect(
					"http://site.com/post/list/" + searchstring + "/" + i)
					.get();

			Element paginator = doc.getElementById("paginator");
			Elements pglinks = paginator.select("a");

			for (Element pln : pglinks) {// Each page has a link "Last" that
											// links to the last page. Get the
											// last page number and crawl from
											// first page till the last.
				Elements pgtemp = pln.getElementsContainingText("Last");
				for (Element t : pgtemp) {
					s = t.attr("href").toString();
					s = s.replaceAll("[a-zA-Z]", "");

					s = s.replaceAll("[_/]", "");
					System.out.println(s);
					n = Integer.parseInt(s);
				}
			}

			if (n == 0) { // Edge case. If there's only one page, The link
							// "Last" won't be displayed on the page. In that
							// case, just crawl that single page. If no results
							// returned, 404 Exception is correctly displayed &
							// the program terminates.
				for (Element pln2 : pglinks) {
					Elements pgtemp2 = pln2.getElementsContainingText("1");
					for (Element t : pgtemp2) {
						s = t.attr("href").toString();
						s = s.replaceAll("[a-zA-Z]", "");

						s = s.replaceAll("[_/]", "");
						System.out.println(s);
						n = Integer.parseInt(s);
					}

				}

			}

		}

		catch (Exception e) {
			System.err.println(e.toString());
		}

		System.out.println("No. of pages is " + n);
		System.out.println("http://site.com/post/list/" + searchstring + "/" + i);
		if (n == 0) {
			searchfailed = true;
			System.err.println("Sorry, the search string did not return any result!");
			
		}

		// Unused. Partition pages among threads. Experimenting..
		// partition = (int) Math.floor(numThreads/n);
		// if( n-(partition * numThreads) > 0 ){ mod = n-(partition *
		// numThreads);}

		if (n != 0) {
			try {
				crawlers = new RuleCrawler[n];
				for (int j = 0; j < n; j++)
					crawlers[j] = new RuleCrawler(j + 1, j + 1, searchstring,
							storepath);

				for (int j = 0; j < n; j++)
					crawlers[j].runner.join();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

	}

	/*
	 * void join(rulecrawler crawler){ //Unnecessary. Used inbuilt join function
	 * for efficiency. /*boolean joined=false; int count=0; while(!joined){
	 * for(int i=0; i<n; i++){ if(crawlers[i].finished){ count++; } } if(count
	 * == n) joined=true; if(count != n) Thread.currentThread().yield(); } }
	 */

	void tagcrawl() throws Exception {
		String basepage = "http://site.com/tags/alphabetic?show_all=on&starts_with=";

		int index = 0;

		if (!tagcharspecified) {
			for (int i = index; i < taglist.length(); i++) {
				try {
					Document doc = Jsoup.connect(
							basepage + taglist.charAt(index)).get();
					Element paginator = doc.getElementById("Tagsmain");
					Elements pglinks = paginator.select("a");
					System.out.println("Tags for character"
							+ taglist.charAt(index) + "are: ");
					for (Element pln : pglinks) {
						System.out.println(pln.text());
					}
				}

				catch (Exception e) {
					System.err.println(e.toString());
				}
			}

		}

		else {
			try {
				Document doc = Jsoup.connect(basepage + tagchar).get();
				Element paginator = doc.getElementById("Tagsmain");
				Elements pglinks = paginator.select("a");
				System.out.println("Tags for character" + tagchar + "are: ");
				for (Element pln : pglinks) {
					System.out.println(pln.text());
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		int thr; // Unused. Can be used to specify number of threads by the
					// user.
		String sstr = null, spath = null, temp = null;
		char choice = '\0';
		boolean crawl = true, tagcrawl = false;
		while (crawl == true) {
			System.out.println("Would you like to list tags all tags (A) or tags for specific character (S) or not (N)?");
			temp = in.nextLine();
			if (temp.toLowerCase().equals("a")) {
				tagcharspecified = false;
				tagcrawl = true;
			} else if (temp.toLowerCase().equals("s")) {
				System.out.println("Enter tag character: ");

				temp = in.nextLine();
				temp = temp.toLowerCase();
				tagchar = temp.charAt(0);
				tagcrawl = true;
				tagcharspecified = true;
			} else if (temp.toLowerCase().equals("n")) {
				tagcrawl = false;
			} else
				continue;

			if (tagcrawl) {
				Rule tagcrawler = new Rule();
				tagcrawler.tagcrawl();
			}

			while (sstr == null) {
				System.out.println("Enter search string: ");
				sstr = in.nextLine();
				if (sstr.charAt(0) == '\n')
					sstr = null;
			}

			while (spath == null) {
				System.out.println("Enter store path: ");
				spath = in.nextLine();
			}
			// Unused - Take num_threads from user.
			// System.out.println("Enter number of parallel threads for crawling: ");
			// thr = Integer.parseInt(in.nextLine());

			Rule crawler = new Rule(sstr, spath);
			crawler.crawl();
			// crawler.join(); //Unnecessary. Used inbuilt join to synchronize
			// for efficiency.
			System.out.println("Do you want to start a new crawl? (y/n)");
			temp = in.nextLine();
			choice = temp.charAt(0);
			if (choice == 'n') {
				crawl = false;
			}
			crawl = false;
		}
		in.close();
	}
}

