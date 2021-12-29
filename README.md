# tp1-registration-login-app
## will be updated

HOW TO DEPLOY with Docker:

1. in pom.xml replace last <build> tag with this:
  
  	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
  
2. build your .jar with maven install
3. upload new jar with WinSCP/FileZzila/Other SFTP client to 147.175.105.115/kaduch/docker and rename it to 'registration-login-spring-boot-security-thymeleaf-mysql-0.0.1-SNAPSHOT.jar'
4. run sudo docker build -t registration-login .
5. run sudo docker-compose up
6. ...Profit on http://147.175.105.115:8080/
