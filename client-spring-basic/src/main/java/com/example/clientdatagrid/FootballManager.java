package com.example.clientdatagrid;

import java.io.Console;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;

/**
 * @author Martin Gencur
 */

public class FootballManager {
	
	//
	// Modify these variables to point to your datagrid server
	//
	private String host = "node1.dg.vm";
	private int port = 11222;
	

    private static final String msgTeamMissing = "The specified team \"%s\" does not exist, choose next operation\n";
    private static final String msgEnterTeamName = "Enter team name: ";

    private static final String teamsKey = "teams";

    private Console con;
    private RemoteCacheManager cacheManager;
    private RemoteCache<String, Object> cache;

    public FootballManager(Console con) {
        this.con = con;
        Configuration configuration = new ConfigurationBuilder()
        		.addServer()
        			.host(host)
        			.port(port)
        		.build();
        cacheManager = new RemoteCacheManager(configuration);
        cache = cacheManager.getCache("teams");
        
        
        if(!cache.containsKey(teamsKey)) {
            List<String> teams = new ArrayList<String>();
            Team t = new Team("Barcelona");
            t.addPlayer("Messi");
            t.addPlayer("Pedro");
            t.addPlayer("Puyol");
            cache.put(t.getName(), t);
            teams.add(t.getName());
            cache.put(teamsKey, teams);
        }
    }

    public void addTeam() {
        String teamName = con.readLine(msgEnterTeamName);
        @SuppressWarnings("unchecked")
        List<String> teams = (List<String>) cache.get(teamsKey);
        if (teams == null) {
            teams = new ArrayList<String>();
        }
        Team t = new Team(teamName);
        cache.put(teamName, t);
        teams.add(teamName);
        // maintain a list of teams under common key
        cache.put(teamsKey, teams);
    }

    public void addPlayers() {
        String teamName = con.readLine(msgEnterTeamName);
        String playerName = null;
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            while (!(playerName = con.readLine("Enter player's name (to stop adding, type \"q\"): ")).equals("q")) {
                t.addPlayer(playerName);
            }
            cache.put(teamName, t);
        } else {
            con.printf(msgTeamMissing, teamName);
        }
    }

    public void removePlayer() {
        String playerName = con.readLine("Enter player's name: ");
        String teamName = con.readLine("Enter player's team: ");
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            t.removePlayer(playerName);
            cache.put(teamName, t);
        } else {
            con.printf(msgTeamMissing, teamName);
        }
    }

    public void removeTeam() {
        String teamName = con.readLine(msgEnterTeamName);
        Team t = (Team) cache.get(teamName);
        if (t != null) {
            cache.remove(teamName);
            @SuppressWarnings("unchecked")
            List<String> teams = (List<String>) cache.get(teamsKey);
            if (teams != null) {
                teams.remove(teamName);
            }
            cache.put(teamsKey, teams);
        } else {
            con.printf(msgTeamMissing, teamName);
        }
    }

    public void printTeams() {
        @SuppressWarnings("unchecked")
        List<String> teams = (List<String>) cache.get(teamsKey);
        if (teams != null) {
            for (String teamName : teams) {
                con.printf(cache.get(teamName).toString());
            }
        }
    }

    public void stop() {
        cacheManager.stop();
    }
}
