package com.example.clientdatagrid;

import java.io.IOException;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.commons.marshall.UTF8StringMarshaller;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;


@SpringBootApplication
public class FootballSpringApplication implements CommandLineRunner {
	
	@Value("${datagrid.host}")
	private String host;
	
	@Value("${datagrid.port}")
	private int port;
	
	@Value("${datagrid.cache}")
	private String cacheName;
	
	public static RemoteCacheManager cacheManager;
	public static RemoteCache<String, String> cacheString;
	public static RemoteCache<String, Team> cachePojo;
	public static RemoteCache<String, JsonNode> cacheJsonNode;
	
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
	    
//		Alternativelly, it's possible to request JSON values but marshalled/unmarshalled with a custom value marshaller that returns `org.codehaus.jackson.JsonNode` objects:
//		DataFormat jsonNode = DataFormat.builder()
//				.valueType(MediaType.APPLICATION_JSON)
//				.valueMarshaller((Marshaller) new CustomJacksonMarshaller())
//			.build();
		
		cacheManager = new RemoteCacheManager(configuration);
		cachePojo = cacheManager.getCache(cacheName);
	    cacheString = cacheManager.getCache(cacheName).withDataFormat(jsonString);
//	    cacheJsonNode = cacheManager.getCache("default").withDataFormat(jsonNode);

	    registerSchemas(cacheManager);
	    
	    
	    
	    // Load information to the cache in several formats (POJO, JSON string)
	    Team team1 = new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
	    Team team2 = new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"});
	    Team team3 = new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"});
	    
		cachePojo.put(team1.getTeamName(), team1);
		cachePojo.put(team2.getTeamName(), team2);
		cacheString.put(team3.getTeamName(), team3.toJsonString());

		log.info("-------> Teams loaded (POJO): " + cachePojo.keySet().toString());
		log.info("-------> Teams loaded (String): " + cacheString.keySet().toString());

		
		
		
		// Queries to the RemoteCache <String, Team>
		QueryFactory queryFactoryPojo = Search.getQueryFactory(cachePojo);

		Query query1 = queryFactoryPojo.from(Team.class).having("teamName").like("Barcelona").build(); // Only for non-analyzed fields. Query DSL does not manage Full-text queries
		Query query2 = queryFactoryPojo.create("from com.example.clientdatagrid.Team where teamName = 'Barcelona'"); // Use ":" for analyzed and "=" for non-analyzed
		Query query3 = queryFactoryPojo.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'"); // Use ":" for analyzed and "=" for non-analyzed

		log.info("----> Queries to the RemoteCache <String, Team>");
		log.info("-------> Query 1: " + query1.list().toString());
		log.info("-------> Query 2: " + query2.list().toString());
		log.info("-------> Query 3: " + query3.list().toString());
		
		
		
		
		// Queries to the RemoteCache <String, Team>
		QueryFactory queryFactoryString = Search.getQueryFactory(cacheString);

		Query query4 = queryFactoryString.from(Team.class).having("teamName").like("Barcelona").build(); // Only for non-analyzed fields. Query DSL does not manage Full-text queries
		Query query5 = queryFactoryString.create("from com.example.clientdatagrid.Team where teamName = 'Barcelona'"); // Use ":" for analyzed and "=" for non-analyzed
		Query query6 = queryFactoryString.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'"); // Use ":" for analyzed and "=" for non-analyzed

		log.info("----> Queries to the RemoteCache <String, String>");
		log.info("-------> Query 1: " + query4.list().toString());
		log.info("-------> Query 2: " + query5.list().toString());
		log.info("-------> Query 3: " + query6.list().toString());


		// Check that data stored with Pojo and String can be retrieved with Pojo and String
		log.info("-------> Barcelona get (POJO): " + cachePojo.get("Barcelona"));
		log.info("-------> Barcelona get (String): " + cacheString.get("Barcelona"));
		log.info("-------> Atleti get (POJO): " + cachePojo.get("Atleti"));
		log.info("-------> Atleti get (String): " + cacheString.get("Atleti"));
		
		
		
//		cache.entrySet().stream().

//		ObjectMapper objectMapper = new ObjectMapper();
//	    String teamAsString = "";
//		try {
//			teamAsString = objectMapper.writeValueAsString(team3);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		log.info("-------> Team3: " + teamAsString);
		


//		cacheJsonNode = cache.withDataFormat(jsonNode);

	}
	
	private void registerSchemas(RemoteCacheManager cacheManager) {
		
		SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
		
		// Register Team schema in the client
		ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
		String memoSchemaFile = "";
		try {
			memoSchemaFile = protoSchemaBuilder.fileName("team.proto").packageName("com.example.clientdatagrid")
					.addClass(Team.class).build(serCtx);
		} catch (ProtoSchemaBuilderException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		log.info("--> Proto schema: " + memoSchemaFile);

		// Register Team schema in the server
		RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
	    metadataCache.put("memo.proto", memoSchemaFile);
		String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
		if (errors != null) {
			throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
		}
	}
}

