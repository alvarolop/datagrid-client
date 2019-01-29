client-spring-basic: Access to the cache via HotRod
=========================================

What is it?
-----------

Hot Rod is a binary TCP client-server protocol used in JBoss Data Grid. The Hot Rod protocol facilitates faster client and server interactions in comparison to other text based protocols and allows clients to make decisions about load balancing, failover and data location operations.

This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using the Hot Rod protocol. It is a simple Football Manager console application allows you to add and remove teams, add players to or remove players from teams, or print a list of the current teams and players using the Hot Rod based connector.


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

2. Install a JDBC driver into JDG (since JDG includes H2 by default, this step may be skipped for the scope of this example). More information can be found in the DataSource Management chapter of the Administration and Configuration Guide for JBoss Enterprise Application Platform on the Customer Portal at <https://access.redhat.com/site/documentation/JBoss_Enterprise_Application_Platform/> . _NOTE: JDG does not support deploying applications so one cannot install it as a deployment._

3. This Quickstart uses JDBC to store the cache. To permit this, it's necessary to alter JDG configuration file (`JDG_HOME/standalone/configuration/standalone.xml`) to contain the following definitions:
   
* Datasource subsystem definition:

    
        <subsystem xmlns="urn:jboss:domain:datasources:4.0">
            <!-- Define this Datasource with jndi name  java:jboss/datasources/ExampleDS -->
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                    <!-- The connection URL uses H2 Database Engine with in-memory database called test -->
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1</connection-url>
                    <!-- JDBC driver name -->
                    <driver>h2</driver>
                    <!-- Credentials -->
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <!-- Define the JDBC driver called 'h2' -->
                <drivers>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>

* Infinispan subsystem definition:

        <subsystem xmlns="urn:infinispan:server:core:8.5" default-cache-container="local">
            <cache-container name="local" default-cache="default">
                <local-cache name="default" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="memcachedCache" start="EAGER">
                    <locking acquire-timeout="30000" concurrency-level="1000" striping="false"/>
                </local-cache>
                <local-cache name="namedCache" start="EAGER"/>

                <!-- ADD a local cache called 'teams' -->

                <local-cache
                    name="teams"
                    start="EAGER"
                    batching="false">

                    <!-- Define the locking isolation of this cache -->
                    <locking
                        acquire-timeout="20000"
                        concurrency-level="500"
                        striping="false" />

                    <!-- Define the JdbcBinaryCacheStores to point to the ExampleDS previously defined -->
                    <string-keyed-jdbc-store datasource="java:jboss/datasources/ExampleDS" passivation="false" preload="false" purge="false">

                        <!-- specifies information about database table/column names and data types -->
                        <string-keyed-table prefix="JDG">
                            <id-column name="id" type="VARCHAR"/>
                            <data-column name="datum" type="BINARY"/>
                            <timestamp-column name="version" type="BIGINT"/>
                        </string-keyed-table>
                    </string-keyed-jdbc-store>
                </local-cache>
                <!-- End of local cache called 'teams' definition -->

            </cache-container>
            <cache-container name="security"/>
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
2. Modify com.example.clientdatagrid.FootballManager providing the correct values of the host and port of your Data Grid server.
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
Basic usage scenarios can look like this (keyboard shortcuts will be shown to you upon start):

        at  -  add a team
        ap  -  add a player to a team
        rt  -  remove a team
        rp  -  remove a player from a team
        p   -  print all teams and players
        q   -  quit
        
Type `q` one more time to exit the application.





# Original Quickstart
This example is not original from this repository, but it is based on the application logic of the hotrod-endpoint example from the jboss-developer repository: https://github.com/jboss-developer/jboss-jdg-quickstarts/blob/jdg-7.3.x/hotrod-endpoint/README.md

Author: Martin Gencur, Tristan Tarrant Level: Intermediate Technologies: Infinispan, Hot Rod Summary: The hotrod-endpoint quickstart demonstrates how to use Infinispan cache remotely using the Hot Rod protocol. Target Product: JDG Product Versions: JDG 7.x

However, the example has been adapted to run inside a Spring Boot application.