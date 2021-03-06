//package com.example.cache.config;
//
//import org.infinispan.client.hotrod.ProtocolVersion;
//import org.infinispan.client.hotrod.RemoteCacheManager;
//import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
//import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
////import org.infinispan.spring.provider.SpringRemoteCacheManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@EnableCaching
//@Configuration
//public class RemoteCacheConfig {
//	
//	@Value("${datagrid.host}")
//	private String host;
//
//	@Value("${datagrid.port}")
//	private int port;
//	
//	@Value("${datagrid.compatibility}")
//	private String compatibility_mode;
//
//	Logger log = LoggerFactory.getLogger(this.getClass());
//	
//	@Bean
//	public SpringRemoteCacheManager cacheManager() {
//		return new SpringRemoteCacheManager(infinispanCacheManager());
//	}
//
//	private RemoteCacheManager infinispanCacheManager() {
//		log.info("-------> Data Grid host: " + host);
//		log.info("-------> Data Grid port: " + port);
//		ConfigurationBuilder config = new ConfigurationBuilder();
//		config.addServer()
//				.host(host)
//				.port(port);
//		
//		if (compatibility_mode.equals("true")) {
//			log.info("-------> Data Grid compatibility mode: " + ProtocolVersion.PROTOCOL_VERSION_25.name());
//			config.version(ProtocolVersion.PROTOCOL_VERSION_25);
//		}
//		return new RemoteCacheManager(config.build());
//	}
//}
