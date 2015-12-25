
enum Category {
}

enum  Index {
    Non, T50, T100
}

enum  Market {
	TW, // Listed Company
    TWO // Over The Counter
}

public class Stock {
	private String number;
	private String name;
	private Market marketType;
	private String category;
	private Index T50_100;
	
	public Stock(String number, String name, Market marketType, String category) {
		this.number = number;
		this.name = name;
		this.category = category;
		this.marketType = marketType;
		this.T50_100 = Index.Non;
	}
	
	public Stock(String number, String name, Market marketType, String category, Index T50_100) {
		this.number = number;
		this.name = name;
		this.category = category;
		this.marketType = marketType;
		this.T50_100 = T50_100;
	}

	public String getNumber() {
		return number;
	}
	public String getName() {
		return name;
	}
	public String getCategory() {
		return category;
	}
	public Market getMarketType() {
		return marketType;
	}
	public Index getT50_100() {
		return T50_100;
	}
}
