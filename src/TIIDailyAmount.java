import java.sql.Date;

//TII: Three Institutional Investors
public class TIIDailyAmount {
	private String stock_number;
	private Date date;
	// fi: Foreign Investment 外資
	private Long FIBuy;
	private Long FISell;
	// it: Investment Trust 投信
	private Long ITBuy;
	private Long ITSell;
	// dealer 自營商 (自行買賣)
	private Long DealerSelfBuy;
	private Long DealerSelfSell;
	// dealer 自營商 (避險)
	private Long DealerHedgingBuy;
	private Long DealerHedgingSell;
	
	public TIIDailyAmount(String stock_number, Date date, Long FIBuy, Long FISell, Long ITBuy, Long ITSell, 
			Long DealerSelfBuy, Long DealerSelfSell, Long DealerHedgingBuy, Long DealerHedgingSell) {
		this.stock_number = stock_number;
		this.date = date;
		this.FIBuy = FIBuy;
		this.FISell = FISell;
		this.ITBuy = ITBuy;
		this.ITSell = ITSell;
		this.DealerSelfBuy = DealerSelfBuy;
		this.DealerSelfSell= DealerSelfSell;
		this.DealerHedgingBuy = DealerHedgingBuy;
		this.DealerHedgingSell = DealerHedgingSell;
	}

	public String getStock_number() {
		return stock_number;
	}

	public Date getDate() {
		return date;
	}

	public Long getFIBuy() {
		return FIBuy;
	}

	public Long getFISell() {
		return FISell;
	}

	public Long getITBuy() {
		return ITBuy;
	}

	public Long getITSell() {
		return ITSell;
	}

	public Long getDealerSelfBuy() {
		return DealerSelfBuy;
	}

	public Long getDealerSelfSell() {
		return DealerSelfSell;
	}

	public Long getDealerHedgingBuy() {
		return DealerHedgingBuy;
	}

	public Long getDealerHedgingSell() {
		return DealerHedgingSell;
	}
	
}
