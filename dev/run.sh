cp ../target/qrCodeDemo.jar ./
cp -r ../target/qrCodeDemo.lib ./
java -jar -Dspring.profiles.active=dev -Dserver.port=9983 qrCodeDemo.jar
