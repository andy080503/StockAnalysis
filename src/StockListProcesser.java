import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StockListProcesser {
	private final String Tag = "[StockListProcesser]";
	private String File_StockTable = "StockTable.html";
	private String File_T50_100 = "T50_100.html";
	private String Link_StockTable;
	private String Link_T50_100;
	private String DataPath;
	private List<Stock> stockList;
	
	public StockListProcesser() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(new Date());
		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("resources/" + propFileName);
		
		try {
			prop.load(inputStream);
			
			DataPath = prop.getProperty("DataPath");
			Link_StockTable = prop.getProperty("Link_StockTable");
			Link_T50_100 = prop.getProperty("Link_T50_100");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.DataPath += date + "/";
		this.stockList = new ArrayList<Stock>();
	}
	
	public void process() {
		downloadLists();
		parsingStockList(new File(DataPath + File_StockTable));
		
	    addAllToDB();
	}
	
	private void downloadLists() {
		try {
			URL url = new URL(Link_StockTable);
			Utility.downloadFile(url, DataPath, File_StockTable);
			
			url = new URL(Link_T50_100);			
			Utility.downloadFile(url, DataPath, File_T50_100);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finish downloadLists()");
	}
	
	private void parsingStockList(File parsingFile) {
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
			
			Market market = Market.Listed;
			String category = "";
			for(int i = 0; i < tableList.get(0).size()/2; i++) {
				for(int j = 0; j < tableList.size(); j ++) {
					List<String> table = tableList.get(j);
					Stock stock;
					int count = i*2;
					
					if(count < table.size()) {
						if(table.get(count).contains("上市") || table.get(count).contains("上櫃")) {
							market = table.get(count).contains("上市") ? Market.Listed : Market.OTC;
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
		
		System.out.println("Finish parsingStockList()");
	}
	
	public void addAllToDB() {
		jdbcMysql mysql = new jdbcMysql();
		
		for(int i = 0; i < stockList.size(); i++) {
			Stock stock = stockList.get(i);
			
			mysql.insertStock(stock);
		}
		
		//mysql.selectAllStock();
		mysql.CloseConnnection();
		
		System.out.println("Finish addAllToDB()");
	}
	
	public List<Stock> getStockList() {
		return this.stockList;
	}
}
