package com.example.clientdatagrid;

import java.util.List;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.UTF8StringMarshaller;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
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
	public static RemoteCache<String, Team> cache;
	
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
		
	    
	    log.info("-------> Converting to RemoteCache<String, String>");
		cacheManager = new RemoteCacheManager(configuration);
	    cache = cacheManager.getCache("default");
	
		
	    Team team1 = new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
	    Team team2 = new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"});
	    Team team3 = new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"});

	    cache.put(team1.getName(), team1);
	    cache.put(team2.getName(), team2);
	    cache.put(team3.getName(), team3);

		log.info("-------> Loaded: " + team1.toJsonString());
		log.info("-------> Loaded: " + team2.toJsonString());
		log.info("-------> Loaded: " + team3.toJsonString());
		
		log.info("-------> Barcelona is: " + cache.get("Barcelona"));
		
	}
}

