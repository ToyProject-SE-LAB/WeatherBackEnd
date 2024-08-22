package vo;

public class MidWeatherInfo {
    private String date; // 예보날짜(오전, 오후)
    private String weather; // 하늘상태

    public MidWeatherInfo(String date, String weather) {
    	this.date = date;
    	this.weather = weather;
    }
    
    public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getWeather() {
		return weather;
	}


	public void setWeather(String weather) {
		this.weather = weather;
	}
}
