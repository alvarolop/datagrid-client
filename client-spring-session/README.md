client-spring-session: Spring Cache and Session
=========================================

What is it?
-----------

Spring Session provides an API and implementations for managing a user’s session information, while also making it trivial to support clustered sessions without being tied to an application container specific solution. This example uses Red Hat Data Grid to back HttpSession using Java based configuration. This example is a modification of the client-spring-cache and, therefore, includes all the functionality of the previous example.


How it works?
-------------

Enabling Spring session backing in the Data Grid cache is as simple as adding `@EnableInfinispanRemoteHttpSession` annotation and the `springCacheManager()` bean in your configuration class:

```java
@EnableCaching
@EnableInfinispanRemoteHttpSession
public class RemoteCacheConfig {
	
    [...]

	@Bean
	public SpringRemoteCacheManagerFactoryBean springCacheManager() {
		return new SpringRemoteCacheManagerFactoryBean();
	}
}
```


System requirements
-------------------

All you need to build this project is Java 8.0 (Java SDK 1.8) or better, Maven 3.0 or better.

The application this project produces is designed to be run on JBoss Data Grid 7.x


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.
 

Configure JDG
-------------

1. Obtain JDG server distribution on Red Hat's Customer Portal at https://access.redhat.com/jbossnetwork/restricted/listSoftware.html

2. Modify the Infinispan subsystem definition to add the student cache: 

        <subsystem xmlns="urn:infinispan:server:core:8.5" default-cache-container="local">
            <cache-container name="local" default-cache="default">
                <local-cache name="default" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="memcachedCache" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="namedCache" start="EAGER"/>

                <!-- ADD a local cache called 'student' -->

                <local-cache name="student" start="EAGER" batching="false"/>
            </cache-container>
        </subsystem>

Start JDG
---------

1. Open a command line and navigate to the root of the JDG directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   $JDG_HOME/bin/standalone.sh
        For Windows: %JDG_HOME%\bin\standalone.bat


Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JDG as described above.
2. Modify application.properties providing the correct values of the `datagrid.host` and `datagrid.port` of your Data Grid server.
3. Open a command line and navigate to the root directory of this quickstart.
4. Type this command to build and deploy the archive:

        mvn clean spring-boot:run


Deploy the Quickstart on JWS/Tomcat 
-----------------------------------

Build the application using the following command:

        mvn clean package

Deploy it to JWS/Tomcat using your favorite technique.


Using the application
---------------------

Interact with this application using your preferred REST method (Web browser, Postman, curl, etc.). For the sake of simplicity, examples use the `curl` command line tool. This example has two sets of APIs: student API (Explained in `client-spring-cache`) and the session API, which allows you to request information about the sessions stored in the cache.

**Get all the keys of the sessions stored in the cache:** `curl localhost:8080/sessions`.

**Get your session ID:** `curl localhost:8080/session`.

_NOTE: Use Postman to modify or request information of your session parameters_

**Get your session attributes:** `curl localhost:8080/session/current/attributes`.

**Add session attribute:** `curl -X POST -H "Content-Type: application/json" --data '{"Hello":"World"}' localhost:8080/session/current`.

**Check if your session has expired:** `curl localhost:8080/session/current/expired`.

