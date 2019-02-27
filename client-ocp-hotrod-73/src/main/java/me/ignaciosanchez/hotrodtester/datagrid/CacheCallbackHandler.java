package me.ignaciosanchez.hotrodtester.datagrid;


import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
 
public class CacheCallbackHandler implements CallbackHandler {
	
    final private String username;
    final private char[] password;
    final private String realm;

    public CacheCallbackHandler (String username, String realm, char[] password) {
        this.username = username;
        this.password = password;
        this.realm = realm;
    }
    
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				((NameCallback) callback).setName(username);
			} else if (callback instanceof PasswordCallback) {
				((PasswordCallback) callback).setPassword(password);
			} else if (callback instanceof RealmCallback) {
				((RealmCallback) callback).setText(realm);
			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
		
	}
}
