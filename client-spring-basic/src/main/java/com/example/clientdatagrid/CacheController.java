package com.example.clientdatagrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Martin Gencur
 */

@RestController
public class CacheController {
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@PostMapping(path = "/team/{teamName}", consumes = "application/json")
	public void addTeam (@PathVariable String teamName) {
		Team team = new Team(teamName, "This is the initial team", new String[]{"Messi", "Pedro", "Puyol"});
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
	public Team getTeam(@PathVariable String teamName) {
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
