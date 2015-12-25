import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

public class DataDownloader {
	private String DataPath;
	private Properties prop;
	//private jdbcMysql mysql;
	
	public DataDownloader() {
		try {
			prop = new Properties();
			String propFileName = "config.properties";
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("resources/" + propFileName);
			prop.load(inputStream);
			DataPath = prop.getProperty("DataPath");
			//mysql = new jdbcMysql();
			
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void downloadMonthCloseData(String number, String year, String month) {
		LinkedHashMap<String, String> table = new LinkedHashMap<String, String>();
		
		try {
			String urlString = prop.getProperty("MonthCloseURL");
			urlString = urlString.replace("[number]", number).replace("[year]", year).replace("[month]", month);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "/";
			String fileName = number.toString() + ".csv";
			Utility.downloadFile(url, path, fileName);
			
			File dataFile = new File(path + fileName);
			
			table = parsingMonthCloseData(dataFile);
			
			dataFile.delete();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		Iterator<String> keyIter = table.keySet().iterator();
		while(keyIter.hasNext()) {
			String tempKey = keyIter.next();
			System.out.println("Key:" + tempKey + ", Value:" + table.get(tempKey));
		}
		
		System.out.println("Finish downloadMonthCloseData()");
	}
	
	private LinkedHashMap<String, String> parsingMonthCloseData(File parsingFile) {
		LinkedHashMap<String, String> table = new LinkedHashMap<String, String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			
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
		
		return table;
	}
	
	public void downloadDailyAllCloseData(String year, String month, String day) {
		try {
			String urlString = prop.getProperty("AllDailyCloseURL");
			urlString = urlString.replace("[year]", year).replace("[month]", month).replace("[day]", day);
			//System.out.println("AllDailyCloseURL:" + urlString);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "_" + day + "/";
			String fileName = "DailyAllCloseData.csv";
			Utility.downloadFile(url, path, fileName);
			
			Date date = new Date(DateFormat.getDateInstance().parse(year + "/" + month + "/" + day).getTime());
			File dataFile = new File(path + fileName);
			
			parsingDailyAllCloseData(dataFile, date);
			
			dataFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Finish downloadDailyAllCloseData()");
	}
	
	private void parsingDailyAllCloseData(File parsingFile, Date date) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("證券代號,證券名稱")) {
					while(!(line = br.readLine()).contains("漲跌符號說明")) {
						ArrayList<String> dataArray = new ArrayList<String>();
						String data;
						while(!line.isEmpty()) {
							if(line.contains(",") && line.contains("\"")) {
								if(line.indexOf("\"") < line.indexOf(",")) {
									int first = line.indexOf("\"");
									int second = line.indexOf("\"", first+1);
									data = line.substring(first, second);
									if(second < line.lastIndexOf(",")) {
										line = line.substring(line.indexOf(",", second+1) + 1);
									} else {
										line = "";
									}
								} else {
									data = line.substring(0, line.indexOf(","));
									line = line.substring(line.indexOf(",") + 1);
								}
							} else if(line.contains(",")) {
								data = line.substring(0, line.indexOf(","));
								line = line.substring(line.indexOf(",") + 1);
							} else {
								data = line;
								line = "";
							}
							
							if(data == null){
								data = "";
							}
							
							dataArray.add(data.replaceAll(",|\"", "").replace("--", "0"));
						}
						/*
						for(int i = 0; i < dataArray.size(); i++){
							System.out.println("dataArray["+i+"]:" + dataArray.get(i));
						}
						*/
						
						
						/* Unused data
						 * dataArray.get(1) = 證券名稱
						 * dataArray.get(10) = 漲跌價差
						 */
						String stock_number = dataArray.get(0);
						Long totalVolume = new Long(dataArray.get(2));
						Integer totalTransactions = new Integer(dataArray.get(3));
						
						// 成交量不為0才寫入DB
						if(totalTransactions != 0) {
							Long totalTurnOver = new Long(dataArray.get(4));
							Double open = new Double(dataArray.get(5));
							Double high = new Double(dataArray.get(6));
							Double low = new Double(dataArray.get(7));
							Double close = new Double(dataArray.get(8));
							//String change = dataArray.get(9);
							String change;
							if(open > close) {
								change = "+";
							} else if(open < close) {
								change = "-";
							} else {
								change = "=";
							}
							Double finalBuyPrc = new Double(dataArray.get(11));
							Integer finalBuyAmt = new Integer(dataArray.get(12));
							Double finalSellPrc = new Double(dataArray.get(13));
							Integer finalSellAmt = new Integer(dataArray.get(14));
							Double PERatio = new Double(dataArray.get(15));
							
							StockDailyData dailyData = new StockDailyData(stock_number, date,
									totalVolume, totalTransactions, totalTurnOver,
									open, high, low, close, change,
									finalBuyPrc, finalBuyAmt, finalSellPrc, finalSellAmt, PERatio);
							
							jdbcMysql mysql = new jdbcMysql();						
							mysql.insertDaily(dailyData);
							mysql.CloseConnnection();
						}
					}
				}
			}
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadALLDailyNetBuySellData(String year, String month, String day) {		
		try {
			String urlString = prop.getProperty("AllDailyNetBuySellURL");
			urlString = urlString.replace("[year]", year).replace("[month]", month).replace("[day]", day);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "_" + day + "/";
			String fileName = "NetBuySell.csv";
			Utility.downloadFile(url, path, fileName);
			
			File dataFile = new File(path + fileName);
			
			parsingNetBuySellData(dataFile);
			
			dataFile.delete();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finish downloadALLDailyNetBuySellData()");
	}
	
	private LinkedHashMap<String, HashMap<String, String>> parsingNetBuySellData(File parsingFile) {
		LinkedHashMap<String, HashMap<String, String>> table = new LinkedHashMap<String, HashMap<String, String>>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return table;
	}

}
