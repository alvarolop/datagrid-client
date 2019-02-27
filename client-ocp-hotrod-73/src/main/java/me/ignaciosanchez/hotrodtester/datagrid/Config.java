package me.ignaciosanchez.hotrodtester.datagrid;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
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

    @Bean
    public SpringRemoteCacheManager cacheManager() {
        return new SpringRemoteCacheManager(infinispanCacheManager());
    }

    @Bean
    public RemoteCacheManager infinispanCacheManager() {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(host)
                .port(Integer.parseInt(port));
        //builder.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(100);

        return new RemoteCacheManager(builder.build());
    }
}