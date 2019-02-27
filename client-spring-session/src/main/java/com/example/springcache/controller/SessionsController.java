package com.example.springcache.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionsController {

	@Autowired
	SpringRemoteCacheManager cacheManager;

	@GetMapping("/sessions")
	public Map<String, String> session(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		String sessionId = request.getSession(true).getId();
		result.put("created:", sessionId);
		// By default Infinispan integration for Spring Session will use 'sessions' cache.
		result.put("active:", cacheManager.getCache("sessions").getNativeCache().keySet().toString());
		return result;
	}
   
	@GetMapping("/session")
	String getSessionId(HttpSession session) {
		return session.getId();
	}
   
	@PostMapping(path = "/session/current", consumes = "application/json")
	public String postStudent(HttpSession session, @RequestBody String json) throws IOException {
		
	    JSONObject jObject  = new JSONObject(json);
	    Iterator iter = jObject.keys();
	    while(iter.hasNext()) {
	    	String key = (String)iter.next();
			session.setAttribute(key, jObject.getString(key));
	    }
		return json;
	}
   
	@SuppressWarnings("unchecked")
	@GetMapping("/session/current/attributes")
	String getSessionAttributes(HttpSession session) {
		// https://docs.spring.io/spring-session/docs/current/api/org/springframework/session/MapSession.html
		RemoteCache<String, MapSession> cache = (RemoteCache<String, MapSession>) cacheManager.getCache("sessions").getNativeCache();
		MapSession mapSession = cache.get(session.getId());
       
		Map<String, String> value = new HashMap<String, String>();
		for (String key: mapSession.getAttributeNames()) {
			value.put(key, mapSession.getAttribute(key).toString());
		}
		return value.toString();
//		return mapSession.getAttributeNames().toString();
   }
	
	@SuppressWarnings("unchecked")
	@GetMapping("/session/current/expired")
	String isSessionExpired(HttpSession session) {
		// https://docs.spring.io/spring-session/docs/current/api/org/springframework/session/MapSession.html
		RemoteCache<String, MapSession> cache = (RemoteCache<String, MapSession>) cacheManager.getCache("sessions").getNativeCache();
		MapSession mapSession = cache.get(session.getId());
		return Boolean.toString(mapSession.isExpired());
	}
}	