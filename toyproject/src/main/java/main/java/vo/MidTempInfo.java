package main.java.vo;

public class MidTempInfo {
	
	    private String date; // 예보날짜
	    private String taMax; // 최고기온
	    private String taMin; // 최저기온
	    
	    public MidTempInfo(String date, String taMax, String taMin){
	    	this.date = date;
	    	this.taMax = taMax;
	    	this.taMin = taMin;
	    }

	    public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTaMax() {
			return taMax;
		}

		public void setTaMax(String taMax) {
			this.taMax = taMax;
		}

		public String getTaMin() {
			return taMin;
		}

		public void setTaMin(String taMin) {
			this.taMin = taMin;
		}
	   
	  
	    @Override
	    public String toString() {
	        return "date=" + date + ", taMax=" + taMax + ", taMin=" + taMin + "";
	    }

}
