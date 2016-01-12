import java.sql.Date;

public class StockDailyData {
	private String stock_number;
	private Date date;
	private Long totalVolume; // 成交股數
	private Integer totalTransactions; // 成交筆數
	private Long totalTurnOver; // 成交金額
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private String change;
	private Double finalBuyPrc;
	private Integer finalBuyAmt;
	private Double finalSellPrc;
	private Integer finalSellAmt;
	private Double PERatio;
	
	public StockDailyData() {
		
	}
	
	public StockDailyData(String stock_number, Date date,
			Long totalVolume, Integer totalTransactions, Long totalTurnOver,
			Double open, Double high, Double low, Double close, String change,
			Double finalBuyPrc, Integer finalBuyAmt, Double finalSellPrc, Integer finalSellAmt, Double PERatio) {
		this.stock_number = stock_number;
		this.date = date;
		this.totalVolume = totalVolume;
		this.totalTransactions = totalTransactions;
		this.totalTurnOver = totalTurnOver;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.change = change;
		this.finalBuyPrc = finalBuyPrc;
		this.finalBuyAmt = finalBuyAmt;
		this.finalSellPrc = finalSellPrc;
		this.finalSellAmt = finalSellAmt;
		this.PERatio = PERatio;
	}

	public String getStock_number() {
		return stock_number;
	}

	public Date getDate() {
		return date;
	}

	public Long getTotalVolume() {
		return totalVolume;
	}

	public Integer getTotalTransactions() {
		return totalTransactions;
	}

	public Long getTotalTurnOver() {
		return totalTurnOver;
	}

	public Double getOpen() {
		return open;
	}

	public Double getHigh() {
		return high;
	}

	public Double getLow() {
		return low;
	}

	public Double getClose() {
		return close;
	}

	public String getChange() {
		return change;
	}

	public Double getFinalBuyPrc() {
		return finalBuyPrc;
	}

	public Integer getFinalBuyAmt() {
		return finalBuyAmt;
	}

	public Double getFinalSellPrc() {
		return finalSellPrc;
	}

	public Integer getFinalSellAmt() {
		return finalSellAmt;
	}

	public Double getPERatio() {
		return PERatio;
	}
}
