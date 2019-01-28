package com.example.clientdatagrid;

import java.io.Console;

import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.clientdatagrid.FootballManager;

@SpringBootApplication
public class ClientDatagridApplication implements CommandLineRunner {
	
    private static final String initialPrompt = "Choose action:\n" + "============= \n" + "at  -  add a team\n"
            + "ap  -  add a player to a team\n" + "rt  -  remove a team\n" + "rp  -  remove a player from a team\n"
            + "p   -  print all teams and players\n" + "q   -  quit\n";
    
//    @Autowired
    FootballManager manager;
	    
	public static void main(String[] args) {
		SpringApplication.run(ClientDatagridApplication.class, args);
	}
	
    @Override
    public void run(String... args) { 
        
        Console con = System.console();
        manager = new FootballManager(con);
        con.printf(initialPrompt);

        while (true) {
            String action = con.readLine(">");
            if ("at".equals(action)) {
                manager.addTeam();
            } else if ("ap".equals(action)) {
                manager.addPlayers();
            } else if ("rt".equals(action)) {
                manager.removeTeam();
            } else if ("rp".equals(action)) {
                manager.removePlayer();
            } else if ("p".equals(action)) {
                manager.printTeams();
            } else if ("q".equals(action)) {
                manager.stop();
                break;
            }
        }
        con.printf("APPLICATION FINISHED");
    }
}

