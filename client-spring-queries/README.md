client-spring-queries
=====================

Example of remote queries using Query DSL and Ickle. 
<!-- TOC -->

- [client-spring-queries](#client-spring-queries)
  - [1. Configure RHDG client](#1-configure-rhdg-client)
  - [2. Configure RHDG Server: Configure the cache for indexing](#2-configure-rhdg-server-configure-the-cache-for-indexing)
  - [3. Configure the client to use Protocol Buffers](#3-configure-the-client-to-use-protocol-buffers)
  - [4. Register the Protobuf schema in the server](#4-register-the-protobuf-schema-in-the-server)
  - [5. Loading the cache with some teams](#5-loading-the-cache-with-some-teams)
  - [6. Querying RHDG](#6-querying-rhdg)
  - [7. Performing queries using JSON and the REST API](#7-performing-queries-using-json-and-the-rest-api)
  - [8. Performing queries using String containing JSON and Java Hot Rod](#8-performing-queries-using-string-containing-json-and-java-hot-rod)
  - [9. Evicting entries using queries](#9-evicting-entries-using-queries)
  - [TODO: 10. Remote script to evict entries.](#todo-10-remote-script-to-evict-entries)
  - [TODO: 11. Protobuf fields](#todo-11-protobuf-fields)

<!-- /TOC -->
## 1. Configure RHDG client

Add the queries dependency in the pom.xml

```xml
<dependency>
    <groupId>org.infinispan</groupId>
    <artifactId>infinispan-remote-query-client</artifactId>
</dependency>
```

## 2. Configure RHDG Server: Configure the cache for indexing

For testing purposes you can use a local cache
```xml
<local-cache name="default" statistics="true">
            <indexing index="ALL" auto-config="true"/>
</local-cache>
```
More information: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_user_guide/index#indexing

## 3. Configure the client to use Protocol Buffers

https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/indexing_querying#storing_protobuf

In order to enable indexing, the entities put in the cache by clients can no longer be opaque binary blobs understood solely by the client.The encoding format used in RHDG in Remote mode is Protocol Buffers. 

First, the client must be configured to use a dedicated marshaller, ProtoStreamMarshaller:

```java
Configuration configuration = new ConfigurationBuilder()
        .addServer()
            .host(host)
            .port(port)
            .marshaller(new ProtoStreamMarshaller())
        .build();

RemoteCacheManager cacheManager = new RemoteCacheManager(configuration);
```

Second, we have to instruct the ProtoStream library on how to marshall your message types. We do so using annotations in the Team class:

```java
@ProtoDoc("@Indexed")
public class Team {

    private String teamName;
    private String description;
    private List<String> players;
    
// ... constructors ....
    
    @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)")
    @ProtoField(number = 1, required = true)
    public String getTeamName() {
        return teamName;
    }
    
    @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.YES)")
    @ProtoField(number = 2, required = true)
    public String getDescription() {
        return description;
    }

    @ProtoDoc("@Field(index=Index.NO, store = Store.NO, analyze = Analyze.NO)")
    @ProtoField(number = 3, collectionImplementation = ArrayList.class)
    public List<String> getPlayers() {
        return players;
    }
    
// ... setters ....

// ... toString() methods ...
}
```

**NOTE**

As we are using protobuf schemas, the class Team does not implement Serializable.

After that the Proto file is automatically generated and loaded to the client context using the following code:

```java
SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(cacheManager);
ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
String memoSchemaFile = protoSchemaBuilder
            .fileName("team.proto")
            .packageName("com.example.clientdatagrid")
            .addClass(Team.class)
        .build(serCtx);
```

The Protobuf annotations of the Team class would generate a .proto file like this:

```
// File name: team.proto
// Scanned classes:
//   com.example.clientdatagrid.Team
package com.example.clientdatagrid;

/**
 * @Indexed
 */
message Team {
   
   /**
    * @Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)
    */
   required string teamName = 1;
   
   /**
    * @Field(index=Index.YES, store = Store.YES, analyze = Analyze.YES)
    */
   required string description = 2;
   
   /**
    * @Field(index=Index.NO, store = Store.NO, analyze = Analyze.NO)
    */
   repeated string players = 3;
}

```
Check the following link to read more about @ProtoDoc and @ProtoField: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.0/html/developer_guide/sect-Protobuf_Encoding#Defining_Protocol_Buffers_Schemas_With_Java_Annotations

Check the following link to read more about the index, store and analyze fields: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.2/html-single/developer_guide/index#field

## 4. Register the Protobuf schema in the server

The server needs to obtain the relevant metadata from the same descriptor (.proto file) as the client. The descriptors are stored in a dedicated cache on the server named '___protobuf_metadata'.

```java
RemoteCache<String, String> metadataCache = cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
metadataCache.put("team.proto", memoSchemaFile);
String errors = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
if (errors != null) {
    throw new IllegalStateException("Some Protobuf schema files contain errors:\n" + errors);
}
```

## 5. Loading the cache with some teams

```java
RemoteCache<String, Team> cacheTeam = cacheManager.getCache(cacheName);

cacheTeam.put("Barcelona", new Team("Barcelona", "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"}));
cacheTeam.put("Madrid", new Team("Madrid", "This is the second team", new String[]{"Benzema", "Ramos", "Bale"}));
cacheTeam.put("Atleti", new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"}));
```

## 6. Querying RHDG

The key part of creating a query is obtaining the QueryFactory for the remote cache using the org.infinispan.client.hotrod.Search.getQueryFactory() method. After that, you can use Query DSL or Ickle.

```java
QueryFactory qf = Search.getQueryFactory(cacheTeam);

Query query1 = queryFactoryPojo.from(Team.class).having("teamName").like("Barcelona").build();
Query query2 = queryFactoryPojo.create("from com.example.clientdatagrid.Team where teamName = 'Barcelona'");
```

Previous code has two examples of queries:

### Query DSL: query1
This is just a wrapper of Ickle that helps you to write queries in a simpler manner, but has less functionality than Ickle. For example, it can only be used for non-analyzed fields (it does not support Full-text queries). 

Check the documentation: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/indexing_querying#query_dsl


### Ickle: query2
This is the most complete way of querying. It allows normal and full-text queries.

When querying a field:
- If a field is analyzed, queries must use **"\<fieldName> : \<fieldValue>"**. E.g. description is analyzed: `"from com.example.clientdatagrid.Team where description : 'This is the initial team'"`
- If the field is not anylized, queries must use **"\<fieldName> = \<fieldValue>"**. E.g. teamName is not analyzed: `"from com.example.clientdatagrid.Team where teamName = 'Barcelona'"`

Check the documentation for more information about queries: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/indexing_querying#query_ickle


## 7. Performing queries using JSON and the REST API

If the REST server endpoint is enabled, you can now perform puts, gets and queries directly using REST:

```bash
# Get all the keys from cache 'default'
$ curl -H "Content-Type: application/json; charset=UTF-8" http://node0.dg.vm:8080/rest/default 

# Get team 'Barcelona' from cache 'default'
$ curl -H "Content-Type: application/json; charset=UTF-8" http://node0.dg.vm:8080/rest/default/Barcelona 

# Delete team 'Barcelona' from cache 'default'
$ curl -X DELETE -H "Content-Type: application/json; charset=UTF-8" http://node0.dg.vm:8080/rest/default/Barcelona

# Query: Get all the teams named 'Barcelona'
$ curl "http://node0.dg.vm:8080/rest/default?action=search&query=from%20com.example.clientdatagrid.Team%20where%20teamName=%27Barcelona%27"

# Query: Get all the teams
$ curl "http://node0.dg.vm:8080/rest/default?action=search&query=from%20com.example.clientdatagrid.Team%20"
```
Use `-v` to show request headers: 
```bash
# Get team 'Barcelona' from cache 'default'
$ curl -v -H "Content-Type: application/json; charset=UTF-8" http://node0.dg.vm:8080/rest/default/Barcelona
```

**IMPORTANT**
When writing a JSON document, a special field _type must be present in the document to identify the protobuf Message corresponding to the document.

```json
{"_type":"com.example.clientdatagrid.Team","teamName":"Barcelona","description":"This is the initial team","players":["Messi","Pedro","Puyol"]}
```


For more information about all the possibilities that the REST API provides, please check: https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/client_server#rest_api


## 8. Performing queries using String containing JSON and Java Hot Rod
It’s possible to decorate remote caches so that all operations can happen with a custom data format. In this case, we are going to use Strings containing JSON. 

```java
DataFormat jsonString = DataFormat.builder()
            .valueType(MediaType.APPLICATION_JSON)
            .valueMarshaller(new UTF8StringMarshaller())
        .build();
RemoteCache<String, String> cacheString = cacheManager.getCache(cacheName).withDataFormat(jsonString);
```

Now you can perform operations using Teams converted to jsonstring and including the `_type`. For that purpose, the Team class contains a method that manually converts a Team to the correct string.

```java
cacheString.put("Atleti", (new Team("Atleti", "This is the third team", new String[]{"Griezmann", "Morata", "Costa"})).toJsonString());
```

Using this configuration, you can perform actions using Team objects or JSON strings interchangeably. This way, you can use the cache normally using Team objects, but you can access the cache using a Java Hot Rod client that does not have the Team class using JSON strings.


## 9. Evicting entries using queries

Using Query DSL or Ickle queries is limited by two factors:

- It is not possible to remove or update entries, only retrieve them.
- The list retrieved only contains the values on the entries, not the keys.

The best option to evict multiple entries based on a query is storing the key under inside the value object and then remove from the cache all the results of the cache:

```java
List<Team> removeList = queryFactoryTeam.create("from com.example.clientdatagrid.Team where teamName = 'Atleti'").list();
log.info("-------> Remove list: " + removeList.toString());
for (Team team : removeList ) {
    cacheTeam.remove(team.getTeamName());
}
```
In the previous example, the key used to store teams in the teamName and, therefore, they can be removed using the teamName field.

The main burden is possibly the roundtrip of the remove() action. In RHDG 8 we will probably have a removeAsync() operation for this purpose.


## TODO: 10. Remote script to evict entries.




## TODO: 11. Protobuf fields

Difference between Analyzed, store, and not.



