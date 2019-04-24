package com.example.clientdatagrid;

import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.UTF8StringMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.clientdatagrid.Team;

/**
 * @author Martin Gencur
 */

@RestController
public class CacheController {
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@PostMapping(path = "/team/{teamName}", consumes = "application/json")
	public void addTeam (@PathVariable String teamName, @RequestBody String team) {
	    FootballSpringApplication.cache.put(teamName, team);
	}
	
	@PostMapping(path = "/team/{teamName}/player/{playerName}", consumes = "application/json") //TODO
	public void addPlayers(@PathVariable String teamName, @PathVariable String playerName) {      
		FootballSpringApplication.cache.get(teamName);
		log.error("This method is not implemented yet");
	}
	
	@GetMapping(path = "/team")
	public String printTeams() {
	    return FootballSpringApplication.cache.values().toString();
	}
	
	@GetMapping(path = "/team/{teamName}")
	public String getTeam(@PathVariable String teamName) {
	    return FootballSpringApplication.cache.get(teamName);
	}
	
	@DeleteMapping(path = "/team/{teamName}")
	public void removeTeam(@PathVariable String teamName) {	
		FootballSpringApplication.cache.remove(teamName);
	}
	
	@DeleteMapping(path = "/team/{teamName}/player/{playerName}") //TODO
	public void removePlayer(@PathVariable String teamName, @PathVariable String playerName) {
		FootballSpringApplication.cache.get(teamName);
		log.error("This method is not implemented yet");
	}
	
	@GetMapping(path = "/team/stop")
	public void stop() {
		FootballSpringApplication.cacheManager.stop();
	}
}
