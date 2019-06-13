/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.clientdatagrid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class Team implements Serializable {

	private static final long serialVersionUID = 1L;
	private String teamName;
    private String description;
    private List<String> players;
    
	public Team() {
	}

    public Team(String teamName) {
        this.teamName = teamName;
        players = new ArrayList<String>();
    }
    
    public Team(String teamName, String description) {
        this.teamName = teamName;
        this.description = description;
        players = new ArrayList<String>();
    }
    
    public Team(String teamName, String description, String[] players) {
        this.teamName = teamName;
        this.description = description;
        this.players = Arrays.asList(players);
    }
    
    @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.NO)")
    @ProtoField(number = 1, required = true)
    public String getTeamName() {
        return teamName;
    }
    
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
    
    @ProtoDoc("@Field(index=Index.YES, store = Store.YES, analyze = Analyze.YES)")
    @ProtoField(number = 2, required = true)
    public String getDescription() {
        return description;
    }
    
	public void setDescription(String description) {
		this.description = description;
	}

    @ProtoDoc("@Field(index=Index.NO, store = Store.NO, analyze = Analyze.NO)")
    @ProtoField(number = 3, collectionImplementation = ArrayList.class)
    public List<String> getPlayers() {
        return players;
    }
    
	public void setPlayers(List<String> players) {
		this.players = players;
	}

    public void addPlayer(String name) {
        players.add(name);
    }

    public void removePlayer(String name) {
        players.remove(name);
    }
    
    public String toJsonString() {
    	StringBuilder b = new StringBuilder("{");
    	b.append("\"_type\":\"" + "com.example.clientdatagrid.Team" + "\",");
        b.append("\"teamName\":\"" + teamName + "\",");
        b.append("\"description\":\"" + description + "\",");
        b.append("\"players\":[");
        Iterator<String> iterator = players.iterator();
        while(iterator.hasNext()){
        	b.append("\"" + iterator.next() + "\"");
        	if (iterator.hasNext())
        		b.append(",");
        	else
        		b.append("]");
        }
        b.append("}"); // Close team
        return b.toString();

    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Team " + teamName + " (" + description + ") with");
        for (String player : players) {
            b.append(" " + player + "");
        }
        return b.toString();
    }
}