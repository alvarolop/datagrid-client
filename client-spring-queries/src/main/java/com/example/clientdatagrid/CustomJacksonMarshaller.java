package com.example.clientdatagrid;

import java.io.IOException;
import java.util.ArrayList;

import org.infinispan.protostream.MessageMarshaller;
//import com.fasterxml.jackson.databind.JsonNode;

//https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/marshalling#user_friendly_externalizers
//https://github.com/infinispan-demos/jdg-remote-query-demo/blob/master/src/main/java/org/jboss/infinispan/demo/marshallers/TaskMarshaller.java

public class CustomJacksonMarshaller implements MessageMarshaller<Team> {
	
	@Override
	public String getTypeName() {
		return "Team";
	}

	@Override
	public Class<Team> getJavaClass() {
		return Team.class;
	}
   
	@Override
	public Team readFrom(ProtoStreamReader reader) throws IOException {
		Team team = new Team();
		team.setTeamName(reader.readString("teamName"));
		team.setDescription(reader.readString("description"));
		team.setPlayers(reader.readCollection("players", new ArrayList<String>(), String.class));
		return team;
	}

	@Override
	public void writeTo(ProtoStreamWriter writer, Team team) throws IOException {
		writer.writeString("teamName", team.getTeamName());
		writer.writeString("description", team.getDescription());
		writer.writeCollection("players", team.getPlayers(),String.class);
	}
}
