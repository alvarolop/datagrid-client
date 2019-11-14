package me.ignaciosanchez.hotrodtester.service;

import org.infinispan.client.hotrod.RemoteCache;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;

@RestController
@org.springframework.context.annotation.Configuration
public class Tester {
	
	private final RemoteCacheManager rcm;

	@Autowired
	public Tester(RemoteCacheManager cacheManager) {
	    this.rcm = cacheManager;
	}

	@Value("${datagrid.host}")
	private String host;

	@Value("${datagrid.port}")
	private String port;

	@GetMapping("/api/info")
	public String info() {
		String value = "Connection to: " + host + " and port " + port; 
		return value;
	}

	@GetMapping("/api/health")
	public String health() {

		if (rcm.isStarted())
			return "Cache Manager is started! " + rcm.getConfiguration().toString();
		else
			return "Cache Manager is not started";
	}

	@GetMapping("/api/reset")
	public String reset() {

		rcm.stop();
		rcm.start();

		return "Cache Manager restarted";
	}

	@GetMapping("/api/cache")
	public String caches() {

		return rcm.getCacheNames().toString();
	}

//	@GetMapping("/api/cache/{cache}/stats")
//	public String stats(@PathVariable(value = "cache") String cacheName) {
//
//		return rcm.getCache(cacheName).stats().getStatsMap().toString();
//	}
	
	@GetMapping("/api/cache/{cache}/stats-client")
	public String clientStats(@PathVariable(value = "cache") String cacheName) {

		return Long.toString(rcm.getCache(cacheName).clientStatistics().getAverageRemoteReadTime());
	}
	
	@GetMapping("/api/cache/{cache}/stats-server")
	public String serverStats(@PathVariable(value = "cache") String cacheName) {

		return rcm.getCache(cacheName).serverStatistics().getStatsMap().toString();
	}

	@GetMapping("/api/cache/{cache}/create")
	public String create(@PathVariable(value = "cache") String cacheName) {

		Configuration config = new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_ASYNC).memory()
				.size(20000).expiration().wakeUpInterval(5000L).maxIdle(120000L).build();

		// rcm.administration().getOrCreateCache(cacheName, config);
		rcm.administration().getOrCreateCache(cacheName, new XMLStringConfiguration(config.toXMLString()));

		return rcm.getCache(cacheName).serverStatistics().getStatsMap().toString();
	}

	@GetMapping("/api/cache/{cache}/put")
	public String put(@PathVariable(value = "cache") String cacheName, @RequestParam(value = "entries") int numEntries,
			@RequestParam(value = "size", required = false) Integer entrySize,
			@RequestParam(value = "minkey", required = false) Integer entryMinkey) {

		RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);
		
//		RemoteCache<String, String> cache2 = rcm.getCache(cacheName);
//		String jsonString = "{\"id\":\"3\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";	      
//		cache2.put("1", jsonString);

		int min = 0;
		if (entryMinkey != null)
			min = entryMinkey;

		int size = 1024;
		if (entrySize != null)
			size = entrySize;

		byte[] bytes = new byte[size];
		Random rnd = new Random();

		for (int i = min; i < (min + numEntries); i++) {

			rnd.nextBytes(bytes);

			cache.put(Integer.toString(i), bytes);
			
		}

		return "OK " + numEntries + " " + entrySize + " " + entryMinkey;
	}

	@GetMapping("/api/cache/{cache}/get")
	public String get(@PathVariable(value = "cache") String cacheName, @RequestParam(value = "entries") int numEntries,
			@RequestParam(value = "minkey", required = false) Integer entryMinkey) {

		RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

		int min = 0;
		if (entryMinkey != null)
			min = entryMinkey;

		for (int i = min; i < (min + numEntries); i++) {
			cache.get(Integer.toString(i));
		}

		return "OK " + cache.get(Integer.toString(1)) + " " + entryMinkey;
	}

	@GetMapping("/api/cache/{cache}/get-single")
	public String getSingle(@PathVariable(value = "cache") String cacheName, @RequestParam(value = "key") int key) {

		RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

		return Arrays.toString(cache.get(Integer.toString(key)));
	}

	@GetMapping("/api/cache/{cache}/remove")
	public String remove(@PathVariable(value = "cache") String cacheName,
			@RequestParam(value = "entries") int numEntries,
			@RequestParam(value = "minkey", required = false) Integer entryMinkey) {

		RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

		int min = 0;
		if (entryMinkey != null)
			min = entryMinkey;

		for (int i = min; i < (min + numEntries); i++) {
			cache.remove(Integer.toString(i));
		}

		return "OK " + numEntries + " " + entryMinkey;
	}

	// putcron, cron, n, minkey, maxkey
}
