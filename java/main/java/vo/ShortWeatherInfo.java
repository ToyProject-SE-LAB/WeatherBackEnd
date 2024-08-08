package main.java.vo;

public class ShortWeatherInfo {
    private int locationCode;
    private String date; // 날짜
    private String time; // 시각
    private String SKY; // 하늘상태
    private String PTY; // 강수형태    
    private String POP; // 강수확률    
    private String TMP; // 온도
    private String REH; // 습도
    private String WSD; // 풍속

    
    
	public int getLocationCode() {
		return locationCode;
	}



	public void setLocationCode(int locationCode) {
		this.locationCode = locationCode;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public String getSKY() {
		return SKY;
	}



	public void setSKY(String sKY) {
		SKY = sKY;
	}



	public String getPTY() {
		return PTY;
	}



	public void setPTY(String pTY) {
		PTY = pTY;
	}



	public String getPOP() {
		return POP;
	}



	public void setPOP(String pOP) {
		POP = pOP;
	}



	public String getTMP() {
		return TMP;
	}



	public void setTMP(String tMP) {
		TMP = tMP;
	}



	public String getREH() {
		return REH;
	}



	public void setREH(String rEH) {
		REH = rEH;
	}



	public String getWSD() {
		return WSD;
	}



	public void setWSD(String wSD) {
		WSD = wSD;
	}



	@Override
    public String toString() {
        return "locationCode = " + locationCode +
                "\ndate = " + date +
                "\ntime = " + time +
                "\nSKY = " + SKY +
                "\nPTY = " + PTY +
                "\nPOP = " + POP +
                "\nTMP = " + TMP +
                "\nREH = " + REH +
                "\nWSD = " + WSD;
    }
}