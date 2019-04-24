package com.example.clientdatagrid;

import java.io.IOException;
import java.util.Scanner;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.UTF8StringMarshaller;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.jgroups.tests.perf.UPerf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.infinispan.protostream.DescriptorParserException;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;


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
		    		.marshaller(new ProtoStreamMarshaller())
	    		.build();
	    		
		log.info("-------> Data Grid host: " + host);
		log.info("-------> Data Grid port: " + port);
		
	    DataFormat jsonString = DataFormat.builder()
	    			.valueType(MediaType.APPLICATION_JSON)
	        		.valueMarshaller(new UTF8StringMarshaller()) // Serializes and deserializes strings and primitives as UTF8 byte arrays.
	    		.build();
	    
	    Team team1 = new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
	    Team team2 = new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"});
	    Team team3 = new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"});
	    
		cacheManager = new RemoteCacheManager(configuration);
	    cache = cacheManager.getCache("default").withDataFormat(jsonString);
	    
		RegisterProtobuf(cacheManager);

	    cache.put(team1.getName(), team1.toJsonString());
	    cache.put(team2.getName(), team2.toJsonString());
	    cache.put(team3.getName(), team3.toJsonString());

		log.info("-------> Loaded: " + cache.keySet().toString());
			
		QueryFactory queryFactory = Search.getQueryFactory(cache);
//		Query query1 = queryFactory.from(String.class).having("name").eq("Barcelona").build();
		
		Query query1 = queryFactory.create("from Team");
		Query query2 = queryFactory.create("from Team where name : 'Barcelona'");	
		
		log.info("-------> This is the result of the query1: " + query1.list().toString());
		log.info("-------> This is the result of the query2: " + query2.list().toString());

	}
	
	private void RegisterProtobuf(RemoteCacheManager cacheManager) {
		
		SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
	    FileDescriptorSource fds = new FileDescriptorSource();
	    
	    try {
			fds.addProtoFiles("/team.proto");
			serCtx.registerProtoFiles(fds);
		} catch (DescriptorParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		// register the schemas with the server too
		RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
//		log.info("-->" + metadataCache.keySet().toString());
//		log.info("-->" + metadataCache.getAll(metadataCache.keySet()).toString());
		//Actually register the proto file
		try (Scanner s = new Scanner(Config.class.getResourceAsStream("/team.proto"), "UTF-8")) {
			String text = s.useDelimiter("\\A").next();
			log.info("Registering proto file:\n" + text);
			try {
				metadataCache.put("team.proto", text);
			} catch (Exception e) {
				log.error("Error registering proto file");
			}
		
			String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
			
			if (errors != null) {
				throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
			}
		}
	}
}

