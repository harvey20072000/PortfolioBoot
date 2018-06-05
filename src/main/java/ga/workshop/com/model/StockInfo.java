package ga.workshop.com.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockInfo {
	
	public StockInfo() {}
	
	public StockInfo(JsonObject jsonObj) {
		this.id = jsonObj.get("c").getAsString();
		this.name = jsonObj.get("n").getAsString();
		this.soldPrice = jsonObj.get("z").getAsString();
		this.openPrice = jsonObj.get("o").getAsString();
		this.lastClosePrice = jsonObj.get("y").getAsString();
		this.maxPrice = jsonObj.get("h").getAsString();
		this.total = jsonObj.get("v").getAsString();
		this.miniPrice = jsonObj.get("l").getAsString();
		this.volume = jsonObj.get("tv").getAsString();
		this.highestPrice = jsonObj.get("u").getAsString();
		this.lowestPrice = jsonObj.get("w").getAsString();
		
		double change = getNumber(this.soldPrice) - 
							getNumber(this.lastClosePrice);
		this.priceChange = formatNumber(change);
		
		this.changeRate = formatNumber(
				change * 100 / getNumber(this.lastClosePrice)) + "%";
		
		String[] b = jsonObj.get("b").getAsString().split("_");
		String[] bv = jsonObj.get("g").getAsString().split("_");
		String[] a = jsonObj.get("a").getAsString().split("_");
		String[] av = jsonObj.get("f").getAsString().split("_");
		if (b.length > 0) {
			this.buyingPrice = b[0];
			this.buyingVolume = bv[0];
		}
		if (a.length > 0) {
			this.offerPrice = a[0];
			this.offerVolume = av[0];
		}
	}
	
	private String id = "";												// 代號
	
	private String name = "";											// 名稱
	
	private String soldPrice;											// 成交
	
	private String buyingPrice;											// 買進
	
	private String priceChange; 										// 漲跌
	
	private String offerPrice;		 									// 賣出
	
	private String changeRate;											// 漲幅
	
	private String openPrice;		 									// 開盤
	
	private String lastClosePrice;			 							// 昨收
	
	private String maxPrice;		 									// 最高
	
	private String total;	 											// 總量
	
	private String miniPrice;		 									// 最低
	
	private String volume;	 											// 單量

	private String highestPrice;			 							// 漲停價
	
	private String lowestPrice;		 									// 跌停價
	
	private String buyingVolume;			 							// 買進量
	
	private String offerVolume;		 									// 賣出量
	
	private long lastUpdateTime = new Date().getTime();  				// 最後更新時間
	
	Comparator<String> comparator = new Comparator<String>() {			// 排序
		public int compare(String s1, String s2) {
			try {
        		return (int)((Double.parseDouble(s1) - 
        				Double.parseDouble(s2))*100000);
        	} catch (Exception e) {
        	}
            return s1.compareTo(s2);
		}			
    };
	private TreeMap<String, String> quotedPrices = new TreeMap<>(comparator);	// 報價
	
	final static NumberFormat formatter = new DecimalFormat("#0.00");
    private static String formatNumber(double d1) {
    	try {
    		return formatter.format(d1);
    	} catch (Exception e) {
    		return "";
    	}
    }
    
    private static double getNumber(Object v1) {
    	try {
    		return Double.parseDouble(v1.toString());
    	} catch (Exception e) {
    		return 0;
    	}
    }
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StockInfo) {
			StockInfo otherSb = (StockInfo) obj;
			if (otherSb.id!=null && otherSb.id.equals(id) &&
					otherSb.lastUpdateTime == this.lastUpdateTime) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "StockInfo [id=" + id + ", name=" + name + ", soldPrice=" + soldPrice + ", buyingPrice=" + buyingPrice
				+ ", priceChange=" + priceChange + ", offerPrice=" + offerPrice + ", changeRate=" + changeRate
				+ ", openPrice=" + openPrice + ", lastClosePrice=" + lastClosePrice + ", maxPrice=" + maxPrice
				+ ", total=" + total + ", miniPrice=" + miniPrice + ", volume=" + volume + ", highestPrice="
				+ highestPrice + ", lowestPrice=" + lowestPrice + ", buyingVolume=" + buyingVolume + ", offerVolume="
				+ offerVolume + ", lastUpdateTime=" + lastUpdateTime + ", comparator=" + comparator + ", quotedPrices="
				+ quotedPrices + "]";
	}
	
	
}
