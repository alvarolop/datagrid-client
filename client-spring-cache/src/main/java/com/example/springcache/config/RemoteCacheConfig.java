package com.example.springcache.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.spring.provider.SpringRemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
