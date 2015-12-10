
public class StockAnalysis {
	public static void main(String[] args) {
		System.out.println("Stock Analysis");
		
		StockListProcesser SD = new StockListProcesser();
		
		SD.process();
		//SD.printAllStock();
		
		DataDownloader dl = new DataDownloader();
		dl.downloadCloseData("4994", "2015", "11");
		dl.downloadNetBuySellData("2014", "12", "25");
	}
}
