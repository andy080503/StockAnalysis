import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
 
public class jdbcMysql {
	private Connection con = null; //Database objects
	private Statement stat = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
	
	private String insert_into_stock = "Insert into stockdata.stock(Name, Number, Market, Category, IndexValue) Values (?,?,?,?,?)";
	private String select_all_from_stock = "select * from stock";
	private String select_from_stock = "SELECT * FROM stock where Number = ";
	
	private String insert_into_dailydata = "Insert into stockdata.dailydata(Stock_Number, Date," +
			" TotalVolume, TotalTransactions, TotalTurnOver, Open, High, Low, Close, `Change`," +
			" FinalBuyPrc, FinalBuyAmt, FinalSellPrc, FinalSellAmt, PERatio) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; // Change 為保留字元
	private String select_from_dailydata = "SELECT * FROM stockdata.dailydata where (Stock_Number, Date) = ";
	
	public jdbcMysql() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//註冊driver
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost/stockdata?useUnicode=true&characterEncoding=Big5",
					"root","Pk123456");

		} catch(ClassNotFoundException e) {
			System.out.println("DriverClassNotFound :"+e.toString());
		} catch(SQLException x) {
			System.out.println("Exception :"+x.toString());
		}
	}
	
	/* [+] Table Stock Operate*/
	public void insertStock(Stock stock) {
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(select_from_stock + "'" + stock.getNumber() + "'");
			
			if(!rs.next()) {
				pst = con.prepareStatement(insert_into_stock);
				pst.setString(1, stock.getName());
				pst.setString(2, stock.getNumber());
				pst.setString(3, stock.getMarketType().name());
				pst.setString(4, stock.getCategory());
				pst.setString(5, stock.getT50_100().name());
				pst.executeUpdate();
			}
		} catch(SQLException e) {
			System.out.println("InsertDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}
	
	public void selectAllStock() {
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(select_all_from_stock);
			System.out.println("Name\t\tNumber\t\tMarket\t\tCategory\t\tIndex");
			
			while(rs.next()) {
				System.out.println(rs.getString("Name") + "\t\t" +
						rs.getString("Number") + "\t\t" + 
						rs.getString("Market") + "\t\t" + 
						rs.getString("Category") + "\t\t" + 
						rs.getString("IndexValue") + "\t\t");
			}
		} catch(SQLException e) {
			System.out.println("DropDB Exception :" + e.toString());
		} finally {
			Close();
		}
	}
	/* [-] Table Stock Operate*/
	
	/* [+] Table DailyData Operate*/
	public void insertDaily(StockDailyData dailyData) {
		try {
			stat = con.createStatement();
			rs = stat.executeQuery(select_from_dailydata + "('" + dailyData.getStock_number() + "' ,date('" + dailyData.getDate().toString() + "'))");
			
			if(!rs.next()) {
				pst = con.prepareStatement(insert_into_dailydata);
				
				pst.setString(1, dailyData.getStock_number());
				pst.setDate(2, dailyData.getDate());
				pst.setLong(3, dailyData.getTotalVolume());
				pst.setDouble(4, dailyData.getTotalTransactions());
				pst.setLong(5, dailyData.getTotalTurnOver());
				pst.setDouble(6, dailyData.getOpen());
				pst.setDouble(7, dailyData.getHigh());
				pst.setDouble(8, dailyData.getLow());
				pst.setDouble(9, dailyData.getClose());
				pst.setString(10, dailyData.getChange());
				pst.setDouble(11, dailyData.getFinalBuyPrc());
				pst.setInt(12, dailyData.getFinalBuyAmt());
				pst.setDouble(13, dailyData.getFinalSellPrc());
				pst.setInt(14, dailyData.getFinalSellAmt());
				pst.setDouble(15, dailyData.getPERatio());
				
				pst.executeUpdate();
			} else if(rs.next() && rs.getString("TotalVolume") != null) {
				
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			Close();
		}
	}
	/* [-] Table DailyData Operate*/
	
	//完整使用完資料庫後,記得要關閉所有Object
	//否則在等待Timeout時,可能會有Connection poor的狀況
	private void Close() {
		try {
			if(rs!=null) {
				rs.close();
				rs = null;
			}
			
			if(stat!=null) {
				stat.close();
				stat = null;
			}
			
			if(pst!=null) {
				pst.close();
				pst = null;
			}
		} catch(SQLException e) {
			System.out.println("Close Exception :" + e.toString());
		}
	}
	
	public void CloseConnnection() {
		try {
			if(rs!=null) {
				rs.close();
				rs = null;
			}
			
			if(stat!=null) {
				stat.close();
				stat = null;
			}
			
			if(pst!=null) {
				pst.close();
				pst = null;
			}
			
			if(con!=null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
