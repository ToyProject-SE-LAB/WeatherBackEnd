package vo;

public class ShortWeatherInfo {

	private String date; // 날짜
    private String description; // 하늘상태  
    private String temp; // 온도
    private String wsd; // 풍속
    private String vec; // 풍향
    private String low; // 일 최저기온
    private String high; // 일 최고기온
    
    
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getWsd() {
		return wsd;
	}
	public void setWsd(String wsd) {
		this.wsd = wsd;
	}
	public String getVec() {
		return vec;
	}
	public void setVec(String vec) {
		this.vec = vec;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}

}