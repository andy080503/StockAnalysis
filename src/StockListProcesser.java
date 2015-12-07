import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StockListProcesser {
	private static String Link_StockTable = "http://www.emega.com.tw/js/StockTable.xls";
	private static String Link_T50_100 = "http://www.emega.com.tw/js/T50_100.xls";
	private static String File_StockTable = "StockTable.html";
	private static String File_T50_100 = "T50_100.html";
	private static String YahooAPI = "http://ichart.finance.yahoo.com/table.csv?s=";
	
	private String XlsPath = "D:/Stock/";
	private List<Stock> stockList;
	
	public StockListProcesser() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(new Date());
		this.XlsPath += date + "/";
		this.stockList = new ArrayList<Stock>();
	}
	
	public void processFlow() {
		downloadLists();
		parsingStockList(new File(XlsPath + File_StockTable));
	}
	
	/* [+] Download file */
	public void downloadLists() {
		try {
			URL url = new URL(Link_StockTable);
			downloadFile(url, XlsPath, File_StockTable);
			
			url = new URL(Link_T50_100);			
			downloadFile(url, XlsPath, File_T50_100);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void downloadFile(URL url, String path, String fileName) {
		try {
			File dir = new File(path);
			FileUtils.forceMkdir(dir);
			File download = new File(path + fileName);
			FileUtils.copyURLToFile(url, download);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/* [-] Download file */
	
	public void parsingStockList(File parsingFile) {
		try {
			Document doc = Jsoup.parse(parsingFile, "big5");
			Elements trElms = doc.getElementsByTag("tr");
			Iterator<Element> trIter = trElms.iterator();
			List<List<String>> tableList = new ArrayList<List<String>>();
			
			while(trIter.hasNext()) {
				List<String> list = new ArrayList<String>();
				Element trE = trIter.next();
				
				Elements tdElms = trE.getElementsByTag("td");
				Iterator<Element> tdIter = tdElms.iterator();
				while(tdIter.hasNext()) {
					Element tdE = tdIter.next();
					String value;
					if(tdE.getElementsByTag("B").isEmpty()) {
						value = tdE.html().toString().replaceAll("&nbsp;","").trim();
					} else {
						value = tdE.getElementsByTag("B").html().toString().replaceAll("&nbsp;","").trim();
					}
					
					if(value != null && !value.isEmpty()) {
						list.add(value);
					}					
				}
				
				tableList.add(list);
			}
			
			Market market = Market.TW;
			String category = "";
			for(int i = 0; i < tableList.get(0).size()/2; i++) {
				for(int j = 0; j < tableList.size(); j ++) {
					List<String> table = tableList.get(j);
					Stock stock;
					int count = i*2;
					
					if(count < table.size()) {
						if(table.get(count).contains("上市") || table.get(count).contains("上櫃")) {
							market = table.get(count).contains("上市") ? Market.TW : Market.TWO;
							category = table.get(count+1);
						} else {
							String number = table.get(count);
							String name = table.get(count+1);
							if(name.contains("＊")) {
								name = name.substring(0, name.indexOf("<"));
								stock = new Stock(number, name, market, category, Index.T50);
							} else if(name.contains("＃")) {
								name = name.substring(0, name.indexOf("<"));
								stock = new Stock(number, name, market, category, Index.T100);
							} else {
								stock = new Stock(number, name, market, category);
							}							
							stockList.add(stock);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void printAllStock() {
		for(int i = 0; i < stockList.size(); i++) {
			Stock stock = stockList.get(i);
			System.out.println("Name: " + stock.getName() + "\tNumber: " + stock.getNumber() + 
					"\tMarket: " + stock.getMarketType() + "\tCategory: " + stock.getCategory() + "\tIndex: " + stock.getT50_100());
		}
	}
	
	public void getData(Stock stock) {
		Document doc = Jsoup.parse(YahooAPI);
	}
}
