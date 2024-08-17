<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Weather App</title>
</head>
<body>
    <h1>Welcome to the Weather App</h1>
    <p id="status">사용자 위치 감지 중...</p>

    <script>
        // 사용자의 위치를 얻기 위한 Geolocation API 사용
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    // 성공적으로 위치를 얻은 경우, 위도와 경도를 서블릿으로 전송
                    var latitude = position.coords.latitude;
                    var longitude = position.coords.longitude;

                    // 위도와 경도를 X, Y 좌표로 변환
                     var convertUrl = "CoordinateServlet?latitude=" + encodeURIComponent(latitude) + "&longitude=" + encodeURIComponent(longitude);

                 	// 비동기적으로 좌표 변환 요청 보내기
                    fetch(convertUrl)
                        .then(response => response.json())
                        .then(data => {
                            var x = data.x;
                            var y = data.y;
                    
                    // 서블릿으로 get 요청 보내기
                    var shortWeatherUrl = "shortweather?x=" + encodeURIComponent(x) + "&y=" + encodeURIComponent(y);
                   	var midWeatherUrl = "midweather?latitude=" + encodeURIComponent(latitude) + "&longitude=" + encodeURIComponent(longitude);
                    var midTempUrl = "midtemp?latitude=" + encodeURIComponent(latitude) + "&longitude=" + encodeURIComponent(longitude);
                    var finedustUrl = "finedust?latitude=" + encodeURIComponent(latitude) + "&longitude=" + encodeURIComponent(longitude);

                    // 비동기적으로 요청 보내기
                    Promise.all([
                        fetch(shortWeatherUrl).then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.text();
                        }),
                        fetch(midWeatherUrl).then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.text();
                        }),
                        fetch(midTempUrl).then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.text();
                        }),
                        fetch(finedustUrl).then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.text();
                        })
                    ])
                    .then(responses => {
                        // 모든 요청이 성공적으로 완료된 후 리디렉션
                        window.location.href = shortWeatherUrl;
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        document.getElementById("status").innerText = "An error occurred.";
                    });
                })
                .catch(error => {
                    console.error('Error fetching coordinates:', error);
                    document.getElementById("status").innerText = "Failed to fetch coordinates.";
                });
        },
                function(error) {
                    // 위치를 가져오지 못한 경우 처리
                    document.getElementById("status").innerText = "Unable to retrieve your location.";
                }
            );
        } else {
            document.getElementById("status").innerText = "Geolocation is not supported by this browser.";
        }
    </script>
</body>
</html>
