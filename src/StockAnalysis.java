import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StockAnalysis {
	public static void main(String[] args) {
		
		try {
			System.out.println("Stock Analysis");
			
			Date today = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String year = dateFormat.format(today).split("/")[0];
			String month = dateFormat.format(today).split("/")[1];
			String day = dateFormat.format(today).split("/")[2];
			
			System.out.println("Date: " + year + "/" + month + "/" + day);
			
			/*
			 * Download and parse stock list 
			 */
			
			StockListProcesser SP = new StockListProcesser();
			SP.process();
			//SD.printAllStock();
			List<Stock> stockList = SP.getStockList();
			
			
			
			/*
			 * Download daily data
			 */
			DataDownloader dl = new DataDownloader();
			
			String startDate = "2016/01/01";
			Date date = dateFormat.parse(startDate);
			
			while(!date.after(today)) {
				System.out.println("\r\nDate: " + dateFormat.format(date));
				
				year = dateFormat.format(date).split("/")[0];
				month = dateFormat.format(date).split("/")[1];
				day = dateFormat.format(date).split("/")[2];
				
				dl.downloadListedDailyCloseData(year, month, day);
				dl.downloadListedDailyAmountData(year, month, day);
				dl.downloadOTCDailyCloseData(year, month, day);
				dl.downloadOTCDailyAmountData(year, month, day);
				
				System.out.println("End " + date.toString());
				
				date.setTime(date.getTime() + (24 * 60 * 60 * 1000L));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
