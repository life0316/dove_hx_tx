package com.haoxi.dove.retrofit.ad;

public class AdviewReqObj {
	/** 广告数量 */
	private int n;
	/** app key */
	private String appid;
	/** 广告类型 0-广告条 1-插屏 4-开屏 5-视频 6-原生 */
	private int pt;
	/** 广告宽度(px) */
	private int w;
	/** 广告高度 */
	private int h;
	/** 外网ip */
	private String ip;
	/** 系统 0-android 1-ios */
	private int os;
	/** 系统版本 */
	private String bdr;
	/** 设备型号 */
	private String tp;
	/** 设备品牌 */
	private String brd;
	/** imei */
	private String sn;
	/** mac */
	private String mac;
	/** android id */
	private String andid;
	/** 网络类型 */
	private String nt;
	/** 服务商 46000 46002=移动 46001=联通 46003=电信 */
	private String nop;
	/** 设备类型 0-手机 1-平板 */
	private int tab;
	/** 是否为测试模式 0-false 1-true */
	private int tm;
	/** 包名 */
	private String pack;
	/** 请求时间戳 */
	private String time;
	/** token=MD5() */
	private String token;
	
	private int html5;
	
	private String ua;

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public int getPt() {
		return pt;
	}

	public void setPt(int pt) {
		this.pt = pt;
	}

	public int getW() {
		return w;
	}

	public int getTm() {
		return tm;
	}

	public void setTm(int tm) {
		this.tm = tm;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getOs() {
		return os;
	}

	public void setOs(int os) {
		this.os = os;
	}

	public String getBdr() {
		return bdr;
	}

	public void setBdr(String bdr) {
		this.bdr = bdr;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getNop() {
		return nop;
	}

	public void setNop(String nop) {
		this.nop = nop;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getHtml5() {
		return html5;
	}

	public void setHtml5(int html5) {
		this.html5 = html5;
	}
	
	public String getUA(){
		return ua;
	}
	
	public void setUA(String ua){
		this.ua = ua;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public String getBrd() {
		return brd;
	}

	public void setBrd(String brd) {
		this.brd = brd;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getAndid() {
		return andid;
	}

	public void setAndid(String andid) {
		this.andid = andid;
	}

	public String getNt() {
		return nt;
	}

	public void setNt(String nt) {
		this.nt = nt;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}
	
}
