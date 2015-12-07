
public class StockAnalysis {
	public static void main(String[] args) {
		System.out.print("Stock Analysis");
		
		StockListProcesser SD = new StockListProcesser();
		
		SD.processFlow();
		SD.printAllStock();
	}
}
