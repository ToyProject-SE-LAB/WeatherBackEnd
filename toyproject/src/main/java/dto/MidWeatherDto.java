package dto;

public class MidWeatherDto {
    private String date; // 예보날짜(오전, 오후)
    private String sky; // 하늘상태
    private String pop; // 강수확률

    
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

}
