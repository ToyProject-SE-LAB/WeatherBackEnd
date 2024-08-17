package vo;

public class FinedustInfo {
	private String date; 		// 측정일
	private String pm10Grade;	// 미세먼지 등급
	private String pm25Grade;	//초미세먼지 등급
	
	
	public FinedustInfo(String date, String pm10Grade, String pm25Grade) {
        this.date = date;
        this.pm10Grade = pm10Grade;
        this.pm25Grade = pm25Grade;
    }
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPm10Grade() {
		return pm10Grade;
	}
	public void setPm10Grade(String pm10Grade) {
		this.pm10Grade = pm10Grade;
	}
	public String getPm25Grade() {
		return pm25Grade;
	}
	public void setPm25Grade(String pm25Grade) {
		this.pm25Grade = pm25Grade;
	}
}
