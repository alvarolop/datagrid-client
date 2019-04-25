package com.example.clientdatagrid;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.infinispan.commons.marshall.Externalizer;

//https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_user_guide/marshalling#user_friendly_externalizers

public class CustomJacksonMarshaller implements Externalizer<Team> {
	
    @Override
    public void writeObject(ObjectOutput output, Team person)
          throws IOException {
       output.writeChars(person.getName());
       output.writeChars(person.getDescription());
    }

    @Override
    public Team readObject(ObjectInput input)
          throws IOException, ClassNotFoundException {
       return new Team((String) input.readObject(), (String) input.readObject());
    }
}
