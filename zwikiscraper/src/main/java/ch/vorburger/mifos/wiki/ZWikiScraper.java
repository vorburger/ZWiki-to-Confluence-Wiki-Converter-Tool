/*
 * Copyright 2010 Michael Vorburger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package ch.vorburger.mifos.wiki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Scraper for a Plone ZWiki.
 * 
 * @see http://mifosforge.jira.com/wiki/display/MIFOSADMIN/ZWiki+to+Confluence+Wiki+Converter+Tool
 * @see http://www.mifos.org/developers/wiki/MigrateDeveloperWiki
 * 
 * @author Michael Vorburger
 * @author Etienne Studer
 */
public class ZWikiScraper {
	
	final String wikiBaseURL;
	final HtmlUnitDriver wd;
	final String wikiLoginName;
	final String wikiPassword;

	class WikiPage {
		String pageID;
		String pageName;
		String pageContent;
		String timeStamp;
	}

	class WikiNode {
		String pageID;
		WikiNode parentNode;
		List<WikiNode> childNodes = new LinkedList<ZWikiScraper.WikiNode>();

		WikiPage getPage() {
			WikiPage page = new WikiPage();
			page.pageID = pageID;
			if (wdGoToEdit(pageID)) {
				page.pageContent = wd.findElement(By.tagName("textarea")).getValue(); // NOT getText(); !!!
				page.pageName = wd.findElement(By.name("title")).getValue();
				page.timeStamp = wd.findElement(By.name("timeStamp")).getValue();
			}
			if ((page.pageName == null) || (page.pageName.isEmpty()))
				page.pageName = page.pageID;
			return page;
		}
		
		void dumpToDirectory(File parentDir, Properties pageId2Name, Properties pageName2Id) throws IOException {
			parentDir.mkdirs();
			WikiPage page = getPage();
			
			if (page.pageContent != null) {
				String fileName = page.pageName + ".rst";
				fileName = fileName.replace('/', '_');
				File contentFile = new File(parentDir, fileName);
				FileWriter fw = null;
				try {
					fw = new FileWriter(contentFile);
					IOUtils.write(page.pageContent, fw);
				} finally {
					if (fw != null)
						fw.close();
				}
				pageId2Name.setProperty(page.pageID, page.pageName);
				pageName2Id.setProperty(page.pageName, page.pageID);
				
/*				// TODO Re-enable, and test... if I actually use pageName & timeStamp
				Properties pageMetadataProperties = new Properties();
				pageMetadataProperties.setProperty("pageName", page.pageName);
				pageMetadataProperties.setProperty("timeStamp", page.timeStamp);
				File propertiesFile = new File(parentDir, page.pageID + ".properties");
				pageMetadataProperties.store(new FileOutputStream(propertiesFile), ZWikiScraper.class.getName());
*/				
			}
			
			if (!childNodes.isEmpty()) {
				File childNodeDir = new File(parentDir, page.pageName);
				childNodeDir.mkdirs();
				for (WikiNode childNode : childNodes) {
					childNode.dumpToDirectory(childNodeDir, pageId2Name, pageName2Id);
				}
			}
		}
	}

	
	/**
	 * Go to the edit page.
	 * Does the login, if needed.
	 * @param pageID zWiki Page ID
	 */
	private boolean wdGoToEdit(String pageID) {
		String url = wikiBaseURL + pageID + "/editform";
		wd.get(url);
		System.out.println(wd.getCurrentUrl()); // TODO Remove
		try {
			wd.findElement(By.tagName("textarea"));
			// So we're really on the edit page, so return.  
			return true;
		}
		catch (NoSuchElementException e1) {
			try {
				// We landed on the Plone login page instead of the Wiki edit, so let's login:
				wd.findElement(By.id("__ac_name")).sendKeys(wikiLoginName);
				wd.findElement(By.id("__ac_password")).sendKeys(wikiPassword);
				wd.findElement(By.name("submit")).click();
				// Now textarea must be found (if the login was valid) ...
				// if we have a NoSuchElementException again here, something is wrong
			} catch (NoSuchElementException e2) {
				// Actually not a login page :( This happens if the TOC has an invalid page ID entry
				return false;
			}
			try {
				wd.findElement(By.tagName("textarea"));
				return true;
			} catch (NoSuchElementException e2) {
				System.out.println(wd.getCurrentUrl());
				System.out.println(wd.getTitle());
				System.out.println(wd.getPageSource());
				throw new IllegalStateException("I am not on the Wiki Edit page as I should be by now, abandoning...");
			}
		}
		
	}
	
	void close() {
		wd.close();
	}

	ZWikiScraper(String wikiBaseURL, String uid, String pwd) {
		this.wikiLoginName = uid;
		this.wikiPassword = pwd;
		this.wikiBaseURL = wikiBaseURL;
		if (!wikiBaseURL.endsWith("/"))
			throw new IllegalArgumentException("wikiBaseURL must end with a slash ('/') but does not: " + wikiBaseURL);

		this.wd = new HtmlUnitDriver(true); // true = JS on; IS NEEDED for authentication & forwarding to work!
		this.wd.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) 
			throw new IllegalArgumentException("Missing uid & pwd, as args");
		
		long startTime = System.currentTimeMillis();
		
		String uid = args[0];
		String pwd = args[1];
		ZWikiScraper s = new ZWikiScraper("http://www.mifos.org/developers/wiki/", uid, pwd);
		List<WikiNode> rootNodes = s.getContents();
		
		long midTime = System.currentTimeMillis() - startTime;

/*		
		WikiPage p = s.getPage("MigrateDeveloperWiki");
		System.out.println(p.pageName);
		System.out.println(p.pageContent);
		System.out.println(s.wd.getPageSource());
*/
		File dumpDir = new File("target/wikiContent");
		Properties pageId2Name = new Properties();
		Properties pageName2Id = new Properties();
		try {
			for (WikiNode node : rootNodes) {
				node.dumpToDirectory(dumpDir, pageId2Name, pageName2Id);
			}
		
		} finally {
			s.close();
			
			saveProperties(pageId2Name, new File(dumpDir, "pageId2Name.properties"));
			saveProperties(pageName2Id, new File(dumpDir, "pageName2Id.properties"));
			
			long stopTime = System.currentTimeMillis();
			long totalTime = stopTime - startTime;
			System.out.println("\n\nZWikiScraper index page fetching and parsing took " + midTime / 1000 + "s");
			System.out.println("\n\nZWikiScraper took " + totalTime / 1000 + "s total (fetched all pages on index)");
		}
	}

	List<WikiNode> getContents() {
		List<WikiNode> rootNodes = new LinkedList<ZWikiScraper.WikiNode>(); 
		
		wd.get(wikiBaseURL + "/FrontPage/contents");
		WebElement all = wd.findElement(By.className("formcontent"));
		WebElement tree = all.findElement(By.tagName("ul"));
		rootNodes.addAll(ulToNodes(tree, null));
		
		WebElement singletonFlatList = all.findElement(By.tagName("div")).findElement(By.tagName("ul"));
		rootNodes.addAll(ulToNodes(singletonFlatList, null));
		
		return rootNodes;
	}

	private List<WikiNode> ulToNodes(WebElement subElements, WikiNode parent) {
		List<WikiNode> nodes = new LinkedList<ZWikiScraper.WikiNode>();

		List<WebElement> lis = subElements.findElements(By.xpath("li"));
		for (WebElement li : lis) {
			WebElement ahref = li.findElement(By.tagName("span")).findElement(By.tagName("a"));
			String href = ahref.getAttribute("href");
			String pageID = wikiURL2PageId(href);
			WikiNode n = new WikiNode();
			n.pageID = pageID;
			n.parentNode = parent;

			try {
				WebElement ul = li.findElement(By.tagName("ul"));
				n.childNodes.addAll(ulToNodes(ul, n));
			} catch (NoSuchElementException e) {
				// No children then, OK.
			}
			
			nodes.add(n);
		}

		return nodes;
	}

	private String wikiURL2PageId(String href) {
		if (href.startsWith(wikiBaseURL)) {
			return href.substring(wikiBaseURL.length());
		} else {
			throw new IllegalArgumentException(href + " does not start with " + wikiBaseURL);
		}
	}
	
	private static void saveProperties(Properties properties, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			properties.store(fos , ZWikiScraper.class.getName());
		} finally {
			fos.close();
		}
	}

}
