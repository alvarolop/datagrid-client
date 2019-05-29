package me.ignaciosanchez.hotrodtester.datagrid;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.spring.provider.SpringRemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class Config {

    @Value("${datagrid.host}")
    private String host;

    @Value("${datagrid.port}")
    private String port;
    
    
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public CacheManager cacheManager() {
        return new SpringRemoteCacheManager(infinispanCacheManager());
    }

    @Bean
    public RemoteCacheManager infinispanCacheManager() {

    	
    	log.info("-------> Data Grid host: " + host);
		log.info("-------> Data Grid port: " + port);
		
		
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(host)
                .port(Integer.parseInt(port));
        //builder.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(100);

        return new RemoteCacheManager(builder.build());
    }
}