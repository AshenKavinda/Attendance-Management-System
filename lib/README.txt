Place the following JAR files in this folder before compiling:

  1. mysql-connector-j-9.x.x.jar  (MySQL JDBC Driver)
     Download: https://dev.mysql.com/downloads/connector/j/
     Choose: Platform Independent -> ZIP archive
     Extract and copy the .jar file here.

  2. JasperReports + Dependencies  (PDF Report Generation)
     Download from Maven Central or:
     https://community.jaspersoft.com/downloads/community-edition/

     Required JARs (minimum set for PDF reports):
       - jasperreports-6.21.3.jar           (core engine)
       - commons-beanutils-1.9.4.jar        (bean utilities)
       - commons-collections4-4.4.jar       (collections)
       - commons-digester-2.1.jar           (XML digester)
       - commons-logging-1.3.0.jar          (logging API)
       - itext-2.1.7.js12.jar              (PDF rendering - JR fork)

     Quick download (all at once from Maven Central):
       Search each artifact at https://search.maven.org/
       Or use: mvn dependency:copy-dependencies with a temp pom.xml

Do NOT commit JAR files to Git (they are ignored by .gitignore).
