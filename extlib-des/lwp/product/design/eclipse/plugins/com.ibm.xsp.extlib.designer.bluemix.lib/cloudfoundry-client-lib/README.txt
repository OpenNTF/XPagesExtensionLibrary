Gary Marjoram - 2016-05-13

The library cloudfoundry-client-lib-1.0.2.jar has been customised, rebuilt and renamed to cloudfoundry-client-lib-1.0.2b.jar. 
TLS v1.0 has been disabled on Bluemix/Datapower and this was stopping Designer from connecting.
To overcome this we've changed RestUtil.java and rebuilt the library to use TLS v1.2.
This fixes the issue.

Build Instructions
------------------
You will need git and maven.
Clone the repo -> "git clone https://github.com/cloudfoundry/cf-java-client.git".
Checkout version 1.02 -> "git checkout v1.0.2".
Copy RestUtil.java to "cloudfoundry-client-lib\src\main\java\org\cloudfoundry\client\lib\util" overwriting the existing file.
Then from the "cloudfoundry-client-lib" directory run "mvn package".
The jar is output to "cloudfoundry-client-lib\target".
