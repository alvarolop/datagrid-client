package com.example.clientdatagrid;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.UTF8StringMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FootballSpringApplication implements CommandLineRunner {
	
	@Value("${datagrid.host}")
	private String host;

	@Value("${datagrid.port}")
	private int port;
	
	public static RemoteCacheManager cacheManager;
	public static RemoteCache<String, String> cache;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public static void main(String[] args) {
		SpringApplication.run(FootballSpringApplication.class, args);
	}
	
    @Override
    public void run(String... args) { 
        Configuration configuration = new ConfigurationBuilder()
        		.addServer()
        			.host(host)
        			.port(port)
        		.build();
        
		log.info("-------> Data Grid host: " + host);
		log.info("-------> Data Grid port: " + port);
		
        DataFormat jsonString = DataFormat.builder()
        			.valueType(MediaType.APPLICATION_JSON)
	        		.valueMarshaller(new UTF8StringMarshaller()) // Serializes and deserializes strings and primitives as UTF8 byte arrays.
        		.build();
        
        log.info("-------> Converting to RemoteCache<String, String>");
		cacheManager = new RemoteCacheManager(configuration);
        cache = cacheManager.getCache("default").withDataFormat(jsonString);

		
        Team team = new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
        cache.put(team.getName(), team.toJsonString());
		log.info("-------> Loaded: " + team.toJsonString());
    }
}

