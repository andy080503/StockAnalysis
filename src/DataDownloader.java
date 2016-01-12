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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

public class DataDownloader {
	private final String Tag = "[DataDownloader]";
	private String DataPath;
	private Properties prop;
	//private jdbcMysql mysql;
	
	private final String URL_daily_close = "ListedDailyCloseURL";
	private final String URL_daily_amount = "ListedDailyAmountData";	
	private final String URL_OTC_daily_close = "OTCDailyCloseURL";
	private final String URL_OTC_daily_amount = "OTCDailyAmountData";
	
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
			
			//dataFile.delete();
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
				if(line.contains("���,���L��")) {
					while(!(line = br.readLine()).contains("�륭�����L��")) {
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
	
	// [+] �W���Ѳ� �C��L����
	public void downloadListedDailyCloseData(String year, String month, String day) {
		try {
			String urlString = prop.getProperty(URL_daily_close);
			urlString = urlString.replace("[year]", year).replace("[month]", month).replace("[day]", day);
			System.out.println("\tListedDailyCloseDataURL:\r\n\t\t" + urlString);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "_" + day + "/";
			String fileName = "DailyListedCloseData.csv";
			Utility.downloadFile(url, path, fileName);
			
			Date date = new Date(DateFormat.getDateInstance().parse(year + "/" + month + "/" + day).getTime());
			File dataFile = new File(path + fileName);
			
			parsingListedDailyCloseData(dataFile, date);
			
			//dataFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\tFinish downloadListedDailyCloseData()");
	}
	
	private void parsingListedDailyCloseData(File parsingFile, Date date) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));			
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("�d�L���")) {
					System.out.println("\t�d�L�L����");
					return;
				}
				
				if(line.contains("�Ҩ�N��,�Ҩ�W��")) {
					while(!(line = br.readLine()).contains("���^�Ÿ�����")) {
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
						
						
						/* 
						 * 0		1		2		3		4		5		6		7		8		9		10		11			12			13			14			15
						 * �Ҩ�N��	�Ҩ�W��	����Ѽ�	���浧��	������B	�}�L��	�̰���	�̧C��	���L��	���^(+/-)	���^���t	�̫ᴦ�ܶR��	�̫ᴦ�ܶR�q	�̫ᴦ�ܽ��	�̫ᴦ�ܽ�q	���q��
						 * 
						 * Unused data
						 * dataArray.get(1) = �Ҩ�W��
						 * dataArray.get(10) = ���^���t
						 */
						String stock_number = dataArray.get(0);
						Long totalVolume = new Long(dataArray.get(2));
						Integer totalTransactions = new Integer(dataArray.get(3));
						
						// ����q����0�~�g�JDB
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
	// [-] �W���Ѳ� �C��L����
	
	// [+] �W���T�j�k�H�C��R��W
	public void downloadListedDailyAmountData(String year, String month, String day) {		
		try {
			String urlString = prop.getProperty(URL_daily_amount);
			urlString = urlString.replace("[year]", year).replace("[month]", month).replace("[day]", day);
			System.out.println("\tListedDailyAmountDataURL:\r\n\t\t" + urlString);
			
			URL url = new URL(urlString);
			String path = DataPath + year + "_" + month + "_" + day + "/";
			String fileName = "ListedDailyAmount.csv";
			Utility.downloadFile(url, path, fileName);
			
			Date date = new Date(DateFormat.getDateInstance().parse(year + "/" + month + "/" + day).getTime());
			File dataFile = new File(path + fileName);
			
			parsingListedDailyAmountData(dataFile, date);
			
			//dataFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\tFinish downloadListedDailyAmountData()");
	}
	
	private void parsingListedDailyAmountData(File parsingFile, Date date) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(parsingFile));
			
			String line;
			while((line = br.readLine()) != null) {
				if(line.contains("�Ҩ�N��")) {
					int count = 0;
					
					while((line = br.readLine()) != null) {
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
							
							dataArray.add(data.replaceAll(",|\"", ""));
						}
						//System.out.println("number: " + dataArray.get(0) + ",\tdataArray.size(): " + dataArray.size());
						
						TIIDailyAmount dailyAmount = null;
						if(dataArray.size() == 11) {
							String stock_number = dataArray.get(0);
							Long FIBuy = new Long(dataArray.get(2));
							Long FISell = new Long(dataArray.get(3));
							Long ITBuy = new Long(dataArray.get(4));
							Long ITSell = new Long(dataArray.get(5));
							Long DealerSelfBuy = new Long(dataArray.get(6));
							Long DealerSelfSell = new Long(dataArray.get(7));
							Long DealerHedgingBuy = new Long(dataArray.get(8));
							Long DealerHedgingSell = new Long(dataArray.get(9));
							
							dailyAmount = new TIIDailyAmount(stock_number, date, FIBuy, FISell, ITBuy, ITSell,
									DealerSelfBuy, DealerSelfSell, DealerHedgingBuy, DealerHedgingSell);
							
							jdbcMysql mysql = new jdbcMysql();						
							mysql.insertDailyAmount(dailyAmount);
							mysql.CloseConnnection();
							
							count++;
						} else if(dataArray.size() == 9) {
							String stock_number = dataArray.get(0);
							Long FIBuy = new Long(dataArray.get(2));
							Long FISell = new Long(dataArray.get(3));
							Long ITBuy = new Long(dataArray.get(4));
							Long ITSell = new Long(dataArray.get(5));
							Long DealerSelfBuy = new Long(dataArray.get(6));
							Long DealerSelfSell = new Long(dataArray.get(7));
							Long DealerHedgingBuy = new Long(0);
							Long DealerHedgingSell = new Long(0);
							
							dailyAmount = new TIIDailyAmount(stock_number, date, FIBuy, FISell, ITBuy, ITSell,
									DealerSelfBuy, DealerSelfSell, DealerHedgingBuy, DealerHedgingSell);
							
							jdbcMysql mysql = new jdbcMysql();						
							mysql.insertDailyAmount(dailyAmount);
							mysql.CloseConnnection();
							
							count++;
						}
					}
					
					if(count == 0) {
						System.out.println("\t�d�L�T�j�k�H�R��W");
					}
				}
			}
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// [-] �W���T�j�k�H�C��R��W
	
	// [+] �W�d�Ѳ� �C��L����
		public void downloadOTCDailyCloseData(String year, String month, String day) {
			try {
				String urlString = prop.getProperty(URL_OTC_daily_close);
				String RoC_year = String.valueOf(Integer.valueOf(year) - 1911);
				urlString = urlString.replace("[RoCyear]", RoC_year).replace("[month]", month).replace("[day]", day);
				System.out.println("\tOTCDailyCloseDataURL:\r\n\t\t" + urlString);
				
				URL url = new URL(urlString);
				String path = DataPath + year + "_" + month + "_" + day + "/";
				String fileName = "DailyOTCCloseData.csv";
				Utility.downloadFile(url, path, fileName);
				
				Date date = new Date(DateFormat.getDateInstance().parse(year + "/" + month + "/" + day).getTime());
				File dataFile = new File(path + fileName);
				
				parsingOTCDailyCloseData(dataFile, date);
				
				//dataFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("\tFinish downloadOTCDailyCloseData()");
		}
		
		private void parsingOTCDailyCloseData(File parsingFile, Date date) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(parsingFile));			
				String line;
				while((line = br.readLine()) != null) {
					if(line.contains("�d�L���")) {
						System.out.println("\t�d�L�L����");
						return;
					}
					
					if(line.contains("�N��,�W��")) {
						while(!(line = br.readLine()).contains("�޲z�Ѳ�") && !line.contains("�W�d�a��")) {
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
								
								dataArray.add(data.replaceAll(",|\"", "").replace("---", "0"));
							}
							/*
							for(int i = 0; i < dataArray.size(); i++){
								System.out.println("dataArray["+i+"]:" + dataArray.get(i));
							}
							
							/* 
							 * 0	1	2	3	4	5	6	7	8		9			10		11		12		13		14		15		16
							 * �N��	�W��	���L 	���^	�}�L 	�̰� 	�̧C	���� 	����Ѽ�  	������B(��)	���浧�� 	�̫�R��	�̫���	�o��Ѽ� 	����Ѧһ� 	 ���麦���� 	����^����
							 * 
							 * Unused data : 2, 14, 15, 16, 17
							 */
							if(dataArray.size() == 17) {
								String stock_number = dataArray.get(0);
								Long totalVolume = new Long(dataArray.get(8));
								Integer totalTransactions = new Integer(dataArray.get(10));
								
								// ���浧�Ƥ���0�~�g�JDB
								if(totalTransactions != 0) {
									Long totalTurnOver = new Long(dataArray.get(9));
									Double open = new Double(dataArray.get(4));
									Double high = new Double(dataArray.get(2));
									Double low = new Double(dataArray.get(5));
									Double close = new Double(dataArray.get(6));
									String change;
									if(open > close) {
										change = "+";
									} else if(open < close) {
										change = "-";
									} else {
										change = "=";
									}
									Double finalBuyPrc = new Double(dataArray.get(11));
									Double finalSellPrc = new Double(dataArray.get(12));
									
									StockDailyData dailyData = new StockDailyData(stock_number, date,
											totalVolume, totalTransactions, totalTurnOver,
											open, high, low, close, change,
											finalBuyPrc, -1, finalSellPrc, -1, -1.0);
									
									jdbcMysql mysql = new jdbcMysql();						
									mysql.insertDaily(dailyData);
									mysql.CloseConnnection();
								}
							}
						}
					}
				}
				
				br.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// [-] �W�d�Ѳ� �C��L����
		
		// [+] �W�d�T�j�k�H�C��R��W
		public void downloadOTCDailyAmountData(String year, String month, String day) {		
			try {
				String urlString = prop.getProperty(URL_OTC_daily_amount);
				String RoC_year = String.valueOf(Integer.valueOf(year) - 1911);
				urlString = urlString.replace("[RoCyear]", RoC_year).replace("[month]", month).replace("[day]", day);
				System.out.println("\tOTCDailyAmountDataURL:\r\n\t\t" + urlString);
				
				URL url = new URL(urlString);
				String path = DataPath + year + "_" + month + "_" + day + "/";
				String fileName = "OTCDailyAmount.csv";
				Utility.downloadFile(url, path, fileName);
				
				Date date = new Date(DateFormat.getDateInstance().parse(year + "/" + month + "/" + day).getTime());
				File dataFile = new File(path + fileName);
				
				parsingOTCDailyAmountData(dataFile, date);
				
				//dataFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("\tFinish downloadOTCDailyAmountData()");
		}
		
		private void parsingOTCDailyAmountData(File parsingFile, Date date) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(parsingFile));
				
				String line;
				while((line = br.readLine()) != null) {
					if(line.contains("�N��,�W��")) {
						int count = 0;
						
						while((line = br.readLine()) != null) {
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
								
								dataArray.add(data.replaceAll(",|\"", "").replaceAll(" ", ""));
							}
							//System.out.println("number: " + dataArray.get(0) + ",\tdataArray.size(): " + dataArray.size());
							
							/* 
							 * 0	1	2			3			4				5			6		7			8
							 * �N��	�W��	�~��γ���R�Ѽ�	�~��γ����Ѽ�	�~��γ���b�R�Ѽ�	��H�R�i�Ѽ�	��H��Ѽ�	��H�b�R�Ѽ�	����b�R�Ѽ�	
							 * 9				10				11					12				13				14				15
							 * �����(�ۦ�R��)�R�Ѽ�	�����(�ۦ�R��)��Ѽ�	�����(�ۦ�R��)�b�R�Ѽ�	�����(���I)�R�Ѽ�	�����(���I)��Ѽ�	�����(���I)�b�R�Ѽ�	�T�j�k�H�R��W�Ѽ�
							 * 
							 * Unused data : 4, 7, 8, 11, 13 
							 */
							
							TIIDailyAmount dailyAmount = null;
							if(dataArray.size() == 16) {
								String stock_number = dataArray.get(0);
								Long FIBuy = new Long(dataArray.get(2));
								Long FISell = new Long(dataArray.get(3));
								Long ITBuy = new Long(dataArray.get(5));
								Long ITSell = new Long(dataArray.get(6));
								Long DealerSelfBuy = new Long(dataArray.get(9));
								Long DealerSelfSell = new Long(dataArray.get(10));
								Long DealerHedgingBuy = new Long(dataArray.get(12));
								Long DealerHedgingSell = new Long(dataArray.get(13));
								
								dailyAmount = new TIIDailyAmount(stock_number, date, FIBuy, FISell, ITBuy, ITSell,
										DealerSelfBuy, DealerSelfSell, DealerHedgingBuy, DealerHedgingSell);
								
								jdbcMysql mysql = new jdbcMysql();						
								mysql.insertDailyAmount(dailyAmount);
								mysql.CloseConnnection();
								
								count++;
							}
						}
						
						if(count == 0) {
							System.out.println("\t�d�Lo�T�j�k�H�R��W");
						}
					}
				}
				
				br.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// [-] �W�d�T�j�k�H�C��R��W

}
