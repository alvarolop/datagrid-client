package com.example.clientdatagrid;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;

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
	public static RemoteCache<String, Team> cacheTeam;
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
		
//	    DataFormat jsonString = DataFormat.builder()
//	    			.valueType(MediaType.APPLICATION_JSON)
//	        		.valueMarshaller(new UTF8StringMarshaller()) // Serializes and deserializes strings and primitives as UTF8 byte arrays.
//	    		.build();
	    
//		Alternativelly, it's possible to request JSON values but marshalled/unmarshalled with a custom value marshaller that returns `org.codehaus.jackson.JsonNode` objects:
//		DataFormat jsonNode = DataFormat.builder()
//				.valueType(MediaType.APPLICATION_JSON)
//				.valueMarshaller((Marshaller) new CustomJacksonMarshaller())
//			.build();
		
		cacheManager = new RemoteCacheManager(configuration);
		cacheTeam = cacheManager.getCache(cacheName);
//	    cacheString = cacheManager.getCache(cacheName).withDataFormat(jsonString);
////	    cacheJsonNode = cacheManager.getCache("default").withDataFormat(jsonNode);

	    registerSchemas(cacheManager);
	    registerScripts(cacheManager);
	    log.info("-------> Schemas and scripts registered");
	    
	    
	    
	    // Load information to the cache in several formats (Team, JSON string)
	    cacheTeam.put("Barcelona", new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"}));
	    cacheTeam.put("Madrid", new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"}));
//		cacheString.put("Atleti", (new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"})).toJsonString());

		log.info("-------> Teams loaded (Team): " + cacheTeam.keySet().toString());
//		log.info("-------> Teams loaded (String): " + cacheString.keySet().toString());

		
		
		
		// Queries to the RemoteCache <String, Team>
		QueryFactory queryFactoryTeam = Search.getQueryFactory(cacheTeam);

		Query query1 = queryFactoryTeam.from(Team.class).having("teamName").like("Barcelona").build(); // Only for non-analyzed fields. Query DSL does not manage Full-text queries
		Query query2 = queryFactoryTeam.create("from com.example.clientdatagrid.Team where teamName = 'Barcelona'"); // Use ":" for analyzed and "=" for non-analyzed
//		Query query3 = queryFactoryTeam.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'"); // Use ":" for analyzed and "=" for non-analyzed

		log.info("----> Queries to the RemoteCache <String, Team>");
		log.info("-------> Query 1: " + query1.list().toString());
		log.info("-------> Query 2: " + query2.list().toString());
//		log.info("-------> Query 3: " + query3.list().toString());
		
		
		
		
		// Queries to the RemoteCache <String, Team>
//		QueryFactory queryFactoryString = Search.getQueryFactory(cacheString);
//
//		Query query4 = queryFactoryString.from(Team.class).having("teamName").like("Barcelona").build(); // Only for non-analyzed fields. Query DSL does not manage Full-text queries
//		Query query5 = queryFactoryString.create("from com.example.clientdatagrid.Team where teamName = 'Barcelona'"); // Use ":" for analyzed and "=" for non-analyzed
//		Query query6 = queryFactoryString.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'"); // Use ":" for analyzed and "=" for non-analyzed
//
//		log.info("----> Queries to the RemoteCache <String, String>");
//		log.info("-------> Query 4: " + query4.list().toString());
//		log.info("-------> Query 5: " + query5.list().toString());
//		log.info("-------> Query 6: " + query6.list().toString());


//		// Check that data stored with Team and String can be retrieved with Team and String
//		log.info("-------> Barcelona get (Team): " + cacheTeam.get("Barcelona"));
//		log.info("-------> Barcelona get (String): " + cacheString.get("Barcelona"));
//		log.info("-------> Atleti get (Team): " + cacheTeam.get("Atleti"));
//		log.info("-------> Atleti get (String): " + cacheString.get("Atleti"));
//		
//		
//		// Example: How to remove all the teams with name == Atleti
//		log.info("-------> Query 7: " + queryFactoryString.create("from com.example.clientdatagrid.Team").list().toString());
//		List<Team> removeList = queryFactoryTeam.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'").list();
//		log.info("-------> Remove list: " + removeList.toString());
//		for (Team team : removeList ) {
//			cacheTeam.remove(team.getTeamName());
//		}
//		
//		List<Object[]> results =  queryFactoryTeam.create("select teamName from com.example.clientdatagrid.Team where country = 'Spain'").list();
//		log.info("-------> List of teams: " + results.toString());
//		for (Object[] team : results ) {
//			log.info("-------> " + cacheTeam.get(team[0]).toString()); 
//			cacheTeam.remove(team[0]);
//		}
//		
//		
//		Set<String> teams = queryFactoryTeam.create("select teamName from com.example.clientdatagrid.Team where country = 'Spain'")
//				   .<Object[]>list()
//				   .stream().map(row -> (String) row[0])
//				   .collect(Collectors.toSet());
//
//		cacheTeam.keySet().removeAll(teams);
//
//		log.info("-------> Query 8(String): " + queryFactoryString.create("select teamName from com.example.clientdatagrid.Team").list().toString());
//		log.info("-------> Query 8(Team): " + queryFactoryString.create("select teamName from com.example.clientdatagrid.Team").list().toString());
//		
//
//
//		
//		// Use remote scripts to remove tasks
//		
//		
//        Map<String, Object> params = new HashMap<>();
//        params.put("key", "myKey");
//        params.put("value", "myValue");
// 
//        log.info("-------> Get \"myKey\": " + cacheString.get("myKey"));
//        Object result = cacheTeam.execute("removeEntries.js", params);
//        log.info("-------> Get \"myKey\": " + cacheString.get("myKey"));
//		
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
//		serCtx.registerMarshaller(new CustomJacksonMarshaller());

//		log.info("--> Proto schema: " + memoSchemaFile);

		// Register Team schema in the server
		RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
	    metadataCache.put("team.proto", memoSchemaFile);
		String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
		if (errors != null) {
			throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
		}
	}
	
	private void registerScripts(RemoteCacheManager cacheManager) {

        String script = "// mode=local,language=javascript\n"
//                + "key*value\n"
                + "var cache = cacheManager.getCache(\"default\");\n"
//                + "cache.clear();\n"
                + ""
                + "cache.remove(key, value);";
		
        RemoteCache<String, String> scriptCache = cacheManager.getCache("___script_cache");
        scriptCache.put("removeEntries.js", script);
        
        
	}
}
