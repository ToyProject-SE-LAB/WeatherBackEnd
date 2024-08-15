package vo;

public class LocationInfo {
	private String regionCode; // 행정구역코드
	private String province;   // 도
	private String city; 	   // 시
	private String district;   // 동
	private String x; 		   // x 좌표값
	private String y; 		   // y 좌표값
	private String latitude;   // 경도
	private String longitude;  // 위도
	private String midTempCode;  // 중기기온코드
	private String stationName;  // 측정소명
	private String midWeatherCode;  // 중기육상코드
	
	public LocationInfo(String regionCode, String province, String city, String district, String x, String y, String latitude, String longitude, String midTempCode, String stationName, String midWeatherCode) {
    	this.regionCode = regionCode;
    	this.province = province;
    	this.city = city;
    	this.district = district;
    	this.x = x;
    	this.y = y;
    	this.latitude = latitude;
    	this.longitude = longitude;
    	this.midTempCode = midTempCode;
    	this.stationName = stationName;
    	this.midWeatherCode = midWeatherCode;
    }

	
	// Getter and Setter
	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getMidTempCode() {
		return midTempCode;
	}
	
	public void setMidTempCode(String midTempCode) {
		this.midTempCode = midTempCode;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getMidWeatherCode() {
		return midWeatherCode;
	}

	public void setMidWeatherCode(String midWeatherCode) {
		this.midWeatherCode = midWeatherCode;
	}

}
