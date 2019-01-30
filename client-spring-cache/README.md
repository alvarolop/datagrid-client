client-spring-cache: Spring Cache
=========================================


What is it?
-----------

Spring Cache provides the layer where other third-party caching implementations can be easily plugged for storing the data, in this case, the Hot Rod client to connect to the Data Grid server. This quickstart demonstrates how to connect remotely to JBoss Data Grid (JDG) to store, retrieve, and remove data from cache using Spring Cache. It is a simple Student Manager REST application that allows you to add, get and remove students. 


How it works?
-------------

All the configuration of Spring Cache to connect to the Data Grid server is made in the RemoteCacheConfig class:
```java
@EnableCaching
@Configuration
public class RemoteCacheConfig {
	
	@Value("${datagrid.host}")
	private String host;

	@Value("${datagrid.port}")
	private int port;

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Bean
	public SpringRemoteCacheManager cacheManager() {
		return new SpringRemoteCacheManager(infinispanCacheManager());
	}

	private RemoteCacheManager infinispanCacheManager() {
		log.info("-------> This is the host: " + host);
		log.info("-------> This is the port: " + port);
		org.infinispan.client.hotrod.configuration.Configuration config = new ConfigurationBuilder()
				.addServer()
					.host(host)
					.port(port)
				.build();
		return new RemoteCacheManager(config);
	}
}
```
Where:
- The caching feature can be declaratively enabled by simply adding the **@EnableCaching** annotation.
- The @Bean annotation declares a CacheManager that will be available project-wide to access the caches.
- The infinispanCacheManager() method initializes the Hot Rod client as it was done in the client-spring-basic example.


After configuring this org.infinispan.spring.provider.SpringRemoteCacheManager you can use regular Spring Cache annotations. 

```java
@Service
@CacheConfig(cacheNames="student")
public class StudentRepo {
	
    Logger log = LoggerFactory.getLogger(this.getClass());
    
	@CachePut(key = "#student.id")
	public Student putStudent(Student student) {
		log.info("---> Creating student with id '" + student.getId() + ": " + student.toString() + "'");
		try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return new Student(student.getId(),student.getName() , student.getEmail());
	}
	
	
	@Cacheable(key = "#id", unless="#result == null")
	public Student getStudentByID(String id) throws StudentNotFoundException {
		log.info("---> Student with id '" + id + "' not cached");
		throw new StudentNotFoundException("Student with id: " + id + " not found.");
	}
	
	@CacheEvict
	public void evictStudentByID(String id) {
		log.info("---> Evict student with id = " + id);
	}
	
	@CacheEvict(allEntries = true)
	public void evictStudents() {
		log.info("---> Evict All Entries.");
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

Interact with this application using your preferred REST method (Web browser, Postman, curl, etc.). For the sake of simplicity, examples use the `curl` command line tool. 

**Get users:** `curl localhost:8080/student`.

**Get only users keys:** `curl localhost:8080/student/keys`.

**Get user with ID:** `curl localhost:8080/student/{id}`.

**Add user:** `curl -X POST -H "Content-Type: application/json" --data '{"id":"1","name":"John Doe","email":"john.doe@example.com"}' localhost:8080/student`.

**Delete users:** `curl -X DELETE localhost:8080/student`.

**Get user with ID:** `curl -X DELETE localhost:8080/student/{id}`.


