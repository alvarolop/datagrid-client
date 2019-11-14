package me.ignaciosanchez.hotrodtester.datagrid;

import javax.annotation.PostConstruct;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

@EnableCaching
@Configuration
public class Config {

    @Value("${datagrid.host}")
    private String host;

    @Value("${datagrid.port}")
    private String port;
    
    private final ObjectProvider<MBeanExporter> mBeanExporter;
    
    Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Bean
    public InfinispanRemoteConfigurer infinispanRemoteConfigurer() {
    	log.info("-------> Connection to: " + host + " and port " + port);
        return () -> new ConfigurationBuilder()
        	.addServer()
            	.host(host)
            	.port(Integer.parseInt(port))
            .statistics()
            	.enable()
            	.jmxEnable()
            .build();
    }
    
    Config (ObjectProvider<MBeanExporter> mBeanExporter) {
		this.mBeanExporter = mBeanExporter;
	}
    
	@PostConstruct
	public void validateMBeans() {
		// Whatever logic that is required to figure out if we should do our thing or not
		this.mBeanExporter
				.ifUnique((exporter) -> exporter.addExcludedBean("remoteCacheManager"));
	}

    
    // The following commented block is another way of configuring RHDG. Use the previous method as is the recommended way for RHDG 7.3
    /*
    @Bean
    public SpringRemoteCacheManager cacheManager() {
        return new SpringRemoteCacheManager(infinispanCacheManager());
    }

    @Bean
    public RemoteCacheManager infinispanCacheManager() {
    	
    	log.info("-------> Connection to: " + host + " and port " + port);
    	
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                	.host(host)
                	.port(Integer.parseInt(port))
                .statistics()
                	.enable()
                	.jmxEnable();
//        builder.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(100);

        return new RemoteCacheManager(builder.build());
    } */
}