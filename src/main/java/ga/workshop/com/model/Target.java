package ga.workshop.com.model;


import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
//@Entity
//@Table(name="Target", schema="PORTFOLIO")
public class Target {

	// 
//	@Id 
//	@Column(name = "STOCK_ID")			
	private String stockId;								// 股票代號
	
//	@Column(name = "STOCK_NAME")
	private String stockName;							// 股票名稱
	
//	@Column(name = "STOCK_INTRO")
//	private String stockIntro;							// 公司簡介
	
//	@Column(name = "MAIN_BUSINESS")
	private String mainBusiness;						// 公司主要事業
	
//	@Column(name = "CATEGORY")
	private String category;							// 產業別
	
//	@Column(name = "MARKET")
	private String market;								// 股票市場別(上市、上櫃)
	
//	@Column(name = "URL")
	private String url;									// 網頁連結
	
//	@Column(name = "LATEST_CLOSING_PRICE_AND_DATE")		
	private String latestClosingPriceAndDate;			// 最新閉盤股價加上日期
	
//	@Column(name = "PASS_THRESHOLD")		
	private String passThreshold;						// 是否適合投資
	
//	@Column(name = "YEARS")
	private List years;									// 年
	
//	@Column(name = "N12M")
	private List n12m;									// 近12個月月份
	
	//
	
//	@Column(name = "FU_ZHAI_RATIO")		
	private Double fu_zhai_ratio;						// 負債總額 (%)
	
//	@Column(name = "FU_ZHAI_RATIOS_Y")		
	private List<Double> fu_zhai_ratios_y;				// 負債總額 (%)(每年)
	
//	@Column(name = "FU_ZHAI_SLOPE")		
	private Double fu_zhai_slope;						// 負債總額 (%)回歸線斜率
	
//	@Column(name = "CUEN_HUO_ROUND_DAY")
	private Double cuen_huo_round_day;					// 存貨周轉天數
	
//	@Column(name = "CUEN_HUO_ROUND_DAYS_Y")
	private List<Double> cuen_huo_round_days_y;			// 存貨周轉天數(每年)
	
//	@Column(name = "CUEN_HUO_ROUND_SLOPE")
	private Double cuen_huo_round_slope;				// 存貨周轉天數回歸線斜率
	
//	@Column(name = "SU_DON_RATIO")
	private Double su_don_ratio;						// 速動比
	
//	@Column(name = "SU_DON_RATIOS_Y")
	private List<Double> su_don_ratios_y;				// 速動比(每年)
	
//	@Column(name = "SU_DON_SLOPE")
	private Double su_don_slope;						// 速動比回歸線斜率
	
//	@Column(name = "YING_YIE_MAO_LI_RATIO")
	private Double ying_yie_mao_li_ratio;				// 營業毛利率
	
//	@Column(name = "YING_YIE_MAO_LI_RATIOS_Y")
	private List<Double> ying_yie_mao_li_ratios_y;		// 營業毛利率(每年)
	
//	@Column(name = "YING_YIE_MAO_LI_SLOPE")
	private Double ying_yie_mao_li_slope;				// 營業毛利率回歸線斜率
	
//	@Column(name = "YING_YIE_LI_YI_RATIO")
	private Double ying_yie_li_yi_ratio;				// 營業利益率
	
//	@Column(name = "YING_YIE_LI_YI_RATIOS_Y")
	private List<Double> ying_yie_li_yi_ratios_y;		// 營業利益率(每年)
	
//	@Column(name = "YING_YIE_LI_YI_SLOPE")
	private Double ying_yie_li_yi_slope;				// 營業利益率回歸線斜率
	
//	@Column(name = "EPS")
	private Double eps;									// eps
	
//	@Column(name = "EPSS_Y")
	private List<Double> epss_y;						// eps(每年)
	
//	@Column(name = "EPS_SLOPE")
	private Double eps_slope;							// eps回歸線斜率
	
//	@Column(name = "BEN_YI_RATIO")
	private Double ben_yi_ratio;						// 本益比
	
//	@Column(name = "BEN_YI_RATIOS_Y")
	private List<Double> ben_yi_ratios_y;				// 本益比(每年)
	
//	@Column(name = "MAX_BEN_YI_RATIO")
	private Double max_ben_yi_ratio;					// 最大本益比
	
//	@Column(name = "MIN_BEN_YI_RATIO")
	private Double min_ben_yi_ratio;					// 最小本益比
	
	//
	
//	@Column(name = "PRICE")
	private Double price;								// 股價
	
//	@Column(name = "CAL_MAX_PRICE")
	private Double cal_max_price;						// 理論最高股價
	
//	@Column(name = "CAL_MIN_PRICE")
	private Double cal_min_price;						// 理論最低股價
	
//	@Column(name = "PRICES_N12M")
	private List<Double> prices_N12M;					// 近12個月股價
	
//	@Column(name = "PRICE_SLOPE")
	private Double price_slope;							// 股價回歸線斜率
	
	//
	
//	@Column(name = "ROA")
	private Double roa;									// ROA
	
//	@Column(name = "ROAS_Y")
	private List<Double> roas_y;						// ROA(每年)
	
//	@Column(name = "ROA_SLOPE")
	private Double roa_slope;							// ROA回歸線斜率
	
//	@Column(name = "ROE")
	private Double roe;									// ROE
	
//	@Column(name = "ROES_Y")
	private List<Double> roes_y;						// ROE(每年)
	
//	@Column(name = "ROE_SLOPE")
	private Double roe_slope;							// ROE回歸線斜率
	
//	@Column(name = "YING_ZAI_RATIO")
	private Double ying_zai_ratio;						// 盈再率
	
//	@Column(name = "YING_ZAI_RATIOS_Y")
	private List<Double> ying_zai_ratios_y;				// 盈再率(每年)
	
//	@Column(name = "YING_ZAI_SLOPE")
	private Double ying_zai_slope;						// 盈再率回歸線斜率
	
//	@Column(name = "YING_SHO_UP_RATIO")
	private Double ying_sho_up_ratio;					// 營收成長率
	
//	@Column(name = "YING_SHO_UP_RATIOS_Y")
	private List<Double> ying_sho_up_ratios_y;			// 營收成長率(每年)
	
//	@Column(name = "YING_SHO_UP_SLOPE")
	private Double ying_sho_up_slope;					// 營收成長率回歸線斜率
	
//	@Column(name = "XIAN_ZIN_RELEASE_RATIO")
	private Double xian_zin_release_ratio;				// 現金股利發放率
	
//	@Column(name = "XIAN_ZIN_RELEASE_RATIOS_Y")
	private List<Double> xian_zin_release_ratios_y;		// 現金股利發放率(每年)
	
//	@Column(name = "XIAN_ZIN_RELEASE_SLOPE")
	private Double xian_zin_release_slope;				// 現金股利發放率回歸線斜率
	
	// 只顯示值
//	@Column(name = "STATIC_ASSETS_RATIOS_Y")
//	@Transient
	private List<Double> static_assets_ratios_y;		// 固定資產比例(每年)
	
	//
	
//	@Column(name = "UPDATE_TIME")						// 更新時間
	private Date updateTime;
	
	public Target(){
		
	}

	public Target(String stockId){
		this.stockId = stockId;
	}
	
}
