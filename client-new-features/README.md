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
	
	@Value("${datagrid.compatibility}")
	private String compatibility_mode;

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Bean
	public SpringRemoteCacheManager cacheManager() {
		return new SpringRemoteCacheManager(infinispanCacheManager());
	}

	private RemoteCacheManager infinispanCacheManager() {
		log.info("-------> Data Grid host: " + host);
		log.info("-------> Data Grid port: " + port);
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addServer()
				.host(host)
				.port(port);
		
		if (compatibility_mode.equals("true")) {
			log.info("-------> Data Grid compatibility mode: " + ProtocolVersion.PROTOCOL_VERSION_25.name());
			config.version(ProtocolVersion.PROTOCOL_VERSION_25);
		}
		return new RemoteCacheManager(config.build());
	}
}
```
Where:
- The caching feature can be declaratively enabled by simply adding the **@EnableCaching** annotation.
- The @Bean annotation declares a CacheManager that will be available project-wide to access the caches.
- The infinispanCacheManager() method initializes the Hot Rod client as it was done in the client-spring-basic example.


After configuring `org.infinispan.spring.remote.provider.SpringRemoteCacheManager` you can use regular Spring Cache annotations. 

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

Configuring the application
---------------------

Once the client application is running on Openshift, you may configure it using the following command:
```bash
oc set env dc <client_dc> DATAGRID_HOST=<server_internal_url> DATAGRID_PORT=11222 DATAGRID_COMPATIBILITY_MODE=<true/false>
```

where DATAGRID_COMPATIBILITY_MODE should be "true" for Data Grid 7.2 and "false" for Data Grid 7.3.


Using the application
---------------------

Interact with this application using your preferred REST method (Web browser, Postman, curl, etc.). For the sake of simplicity, examples use the `curl` command line tool. 

**Get user with ID:** `curl localhost:8080/student/{id}`.

**Add user:** `curl -X POST -H "Content-Type: application/json" --data '{"id":"1","name":"John Doe","email":"john.doe@example.com"}' localhost:8080/student`.

**Delete user with ID:** `curl -X DELETE localhost:8080/student/{id}`.


