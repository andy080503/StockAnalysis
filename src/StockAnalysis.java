import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StockAnalysis {
	public static void main(String[] args) {
		System.out.println("Stock Analysis");
		
		Date date = new Date();
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String year = bartDateFormat.format(date).split("/")[0];
		String month = bartDateFormat.format(date).split("/")[1];
		String day = bartDateFormat.format(date).split("/")[2];
		
		System.out.println("Date: " + year + "/" + month + "/" + day);
		
		StockListProcesser SP = new StockListProcesser();
		
		SP.process();
		//SD.printAllStock();
		
		List<Stock> stockList = SP.getStockList();
		
		DataDownloader dl = new DataDownloader();
		/*
		for(int i = 0; i < stockList.size(); i++) {
			Stock stock = stockList.get(i);
			
			dl.downloadMonthCloseData(stock.getNumber(), year, month);
		}
		*/
		
		dl.downloadDailyAllCloseData(year, month, day);
		dl.downloadALLDailyNetBuySellData(year, month, day);
	}
}
