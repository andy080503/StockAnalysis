import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

public class DataDownloader {
	private String DataPath;
	private Properties prop;
	
	public DataDownloader() {
		try {
			prop = new Properties();
			String propFileName = "config.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("resources/" + propFileName);
			prop.load(inputStream);
			DataPath = prop.getProperty("DataPath");
			
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadCloseData(String number, String year, String month) {
		try {
			String urlString = prop.getProperty("CloseURL");
			urlString = urlString.replace("NumberEntry", number).replace("YearEntry", year).replace("MonthEntry", month);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "/";
			String fileName = number.toString() + ".csv";
			Utility.downloadFile(url, path, fileName);
			
			File data = new File(path + fileName);
			parsingCloseData(data);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void parsingCloseData(File parsingFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			
			LinkedHashMap<String, String> table = new LinkedHashMap<String, String>();
			
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("日期,收盤價")) {
					while(!(line = br.readLine()).contains("月平均收盤價")) {
						String[] data = line.split(",");
						
						table.put(data[0].replaceAll("\\s", ""), data[1]);
					}
				}
			}
			
			br.close();
			
			/*
			Iterator<String> keyIter = table.keySet().iterator();
			while(keyIter.hasNext()) {
				String tempKey = keyIter.next();
				System.out.println("Key:" + tempKey + ", Value:" + table.get(tempKey));
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadNetBuySellData(String year, String month, String Date) {
		try {
			String urlString = prop.getProperty("NetBuySellURL");
			urlString = urlString.replace("YearMonthDateEntry", year + month + Date).replace("YearMonthEntry", year + month);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "_" + Date + "/";
			String fileName = "NetBuySell.csv";
			Utility.downloadFile(url, path, fileName);
			
			File data = new File(path + fileName);
			parsingNetBuySellData(data);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void parsingNetBuySellData(File parsingFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			LinkedHashMap<String, HashMap<String, String>> table = new LinkedHashMap<String, HashMap<String, String>>();
			
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("證券代號")) {
					String[] key = line.split(",");
					
					while((line = br.readLine()) != null) {
						ArrayList<String> dataList = new ArrayList<String>();
						
						while(!line.isEmpty()) {
							String data;
							
							if(line.contains(",")) {
								if(line.contains("\"") && (line.indexOf("\"") < line.indexOf(","))) {
									int first = line.indexOf("\"");
									int second = line.indexOf("\"", (line.indexOf("\"") + 1));
									data = line.substring(first + 1, second);
									if(line.indexOf(",", second) > 0) {
										line = line.substring(line.indexOf(",", second) + 1);
									} else {
										line = "";
									}
									
								} else {
									data = line.substring(0, line.indexOf(","));
									line = line.substring(line.indexOf(",") + 1);
								}
							} else {
								data = line;
								line = "";
							}
							
							dataList.add(data);
						}
						LinkedHashMap<String, String> valueTable = new LinkedHashMap<String, String>();
						
						for(int i = 2; i < key.length; i++) {
							valueTable.put(key[i], dataList.get(i));
						}
						
						table.put(dataList.get(0).replaceAll("\\s", ""), valueTable);
					}
				}
			}
			
			br.close();
			
			/*
			Iterator<String> keyIter = table.keySet().iterator();
			while(keyIter.hasNext()) {
				String tempKey = keyIter.next();
				System.out.println("Key:" + tempKey + ", Value:" + table.get(tempKey));
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
