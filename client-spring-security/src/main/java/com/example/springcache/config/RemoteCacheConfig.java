package com.example.springcache.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.spring.provider.SpringRemoteCacheManager;
import org.infinispan.spring.provider.SpringRemoteCacheManagerFactoryBean;
import org.infinispan.spring.session.configuration.EnableInfinispanRemoteHttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@EnableCaching
@EnableInfinispanRemoteHttpSession
public class RemoteCacheConfig {
	
	@Value("${datagrid.host}")
	private String host;

	@Value("${datagrid.port}")
	private int port;

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Bean
	@Primary
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
	@Bean
	public SpringRemoteCacheManagerFactoryBean springCacheManager() {
		return new SpringRemoteCacheManagerFactoryBean();
	}
	
	// An optional configuration bean that replaces the default cookie for obtaining configuration.
	// For more information refer to Spring Session documentation.
	// Alvaro: This Bean gives one different session each time the tab is refreshed.  
//	@Bean
//	public HttpSessionStrategy httpSessionStrategy() {
//		return new HeaderHttpSessionStrategy();
//	}
}
