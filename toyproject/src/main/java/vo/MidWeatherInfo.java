package vo;

public class MidWeatherInfo {
    private String date; // 예보날짜(오전, 오후)
    private String sky; // 하늘상태
    private String pop; // 강수확률

    public MidWeatherInfo(String date, String sky, String pop) {
    	this.date = date;
    	this.sky = sky;
    	this.pop = pop;
    }
    
    public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getSky() {
		return sky;
	}


	public void setSky(String sky) {
		this.sky = sky;
	}


	public String getPop() {
		return pop;
	}


	public void setPop(String pop) {
		this.pop = pop;
	}

    @Override
    public String toString() {
        return "date=" + date + ", sky=" + sky + ", pop=" + pop + "";
    }
}
