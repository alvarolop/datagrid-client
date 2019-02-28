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
    
    @Value("${datagrid.authentication}")
    private String authentication;
    
    @Value("${datagrid.serverName}")
    private String serverName;
    
    @Value("${datagrid.username}")
    private String username;

    @Value("${datagrid.password}")
    private String password;
    

    @Bean
    public SpringRemoteCacheManager cacheManager() {
        return new SpringRemoteCacheManager(infinispanCacheManager());
    }

    @Bean
    public RemoteCacheManager infinispanCacheManager() {
    	
		String value = "Connection to: " + host + " and port " + port + " with security (" + authentication.contains("true") +  "): "
				+ username + " / " + password + " / " + serverName + ".\n";
		
		System.out.println(value);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host(host)
                .port(Integer.parseInt(port));
    // https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_user_guide/#hotrod_java_client
        if (authentication.contains("true")) {
        	// Option 1
//            builder.security()
//        	.authentication()
//        		.saslMechanism("DIGEST-MD5")
//		        .username(username)
//		        .password(password)
//		        .realm("ApplicationRealm");
            // Option 2
//			builder.security()
//			.authentication()
//				.enable()
//				.serverName(serverName)
//				.saslMechanism("DIGEST-MD5")
//				.callbackHandler(new CacheCallbackHandler(username, "ApplicationRealm", password.toCharArray()));
        }
        //builder.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(100);

        return new RemoteCacheManager(builder.build());
    }
}