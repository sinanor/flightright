Technology choices:
1)DATABASE
Relational database is used for storing the data. H2 database is used for its simplicity, but it is easy to swap other database
products(MySQL, PostgreSQL).
2)Java Framework choices
Spring boot is selected because it is easy to build application also it is easy to create a single runnable jar file that includes everything(tomcat etc)
3)Lombok is used to simplify POJO java classes(getter and setter generation and builder pattern)
4)Spring data was chosen to simplify the code. Spring data do not have merge/persist seperate method, I would've use merge on my update
article endpoint.

Design choices:
1)Persistent class(Article) and Rest service request/response class (ArticleDTO) are different class. Spring framework allow even
Repository methods expose themselves as a rest service with the @RepositoryRestResource annotation. But if would have chosen that approach
then there will be less code to test. I suppose one purpose of the challange to show testing capabilities too. Besides it is good to
seperate Controller, Repository and Persistent Object because they will give more flexibility to change/upgrade etc.

******** storage service functionality is copied from below address **********
https://spring.io/guides/gs/uploading-files/

Improvements
1)Real Database
2)MemberControllerTest contains test for just one endpoint and it should contain all


System requirements: Java 8 JDK, Maven
Running The Application:

to run the application from maven

$mvn spring-boot:run

to create single runnable jar file

$mvn clean package   (this command will create a jar file named  target/codechallange-0.1.0.jar)

after creating the fat jar the application can be run without maven as below

$java -jar target/codechallange-0.1.0.jar

After you run the application embedded tomcat will open the 8080 port and ready to accept rest calls.

To run Automated tests

$mvn test

Rest endpoint addresses

http://localhost:8080/api/member
