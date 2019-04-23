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
		    	.marshaller(new UTF8StringMarshaller())
	    		.build();
	    
//	    GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder()
//	    		builder.serialization().marshaller(myMarshaller); // needs an instance of the marshaller
	    
		log.info("-------> Data Grid host: " + host);
		log.info("-------> Data Grid port: " + port);
		
	    DataFormat jsonString = DataFormat.builder()
	    			.valueType(MediaType.APPLICATION_JSON)
	        		.valueMarshaller(new UTF8StringMarshaller()) // Serializes and deserializes strings and primitives as UTF8 byte arrays.
	    		.build();
	    
	    log.info("-------> Converting to RemoteCache<String, String>");
		cacheManager = new RemoteCacheManager(configuration);
	    cache = cacheManager.getCache("default").withDataFormat(jsonString);
	
		
	    Team team1 = new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
	    Team team2 = new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"});
	    Team team3 = new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"});

	    cache.put(team1.getName(), team1.toJsonString());
	    cache.put(team2.getName(), team2.toJsonString());
	    cache.put(team3.getName(), team3.toJsonString());

		log.info("-------> Loaded: " + team1.toJsonString());
		log.info("-------> Loaded: " + team2.toJsonString());
		log.info("-------> Loaded: " + team3.toJsonString());
		
		log.info("-------> Barcelona is: " + cache.get("Barcelona"));
		
		// RemoteCache<String, String> cache2 = cacheManager.getCache("default");
		QueryFactory queryFactory = Search.getQueryFactory(cache);
		Query query1 = queryFactory.from(String.class).having("name").eq("Barcelona").build();
		
		Query query2 = queryFactory.create("from sample_bank_account.Transaction where name ");

		List<String> list = query1.list();
	}
}

