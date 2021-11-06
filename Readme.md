Using this for AWS Simple-Email-Service

To tests this view the TestEmail class and configure your properties in the test directory

Use properties below, System Properties:

    in cmd ==>  -Dsmtp.properties.path="yourPath"
    in Code ==> System.setProperty("smtp.properties.path", "yourPath");



mvn install the project then add dependency:

    <dependency>
        <groupId>org.mustafa.tools</groupId>
        <artifactId>EmailService</artifactId>
        <version>1.0</version>
    </dependency>
