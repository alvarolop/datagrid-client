package com.example.springcache.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.spring.provider.SpringRemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionsController {

   @Autowired
   SpringRemoteCacheManager cacheManager;

   @GetMapping("/sessions")
   public Map<String, String> session(HttpServletRequest request) {
      Map<String, String> result = new HashMap<>();
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
   
   @SuppressWarnings("unchecked")
   @GetMapping("/session/current/attributes")
   String getSessionAttributes(HttpSession session) {
	   // https://docs.spring.io/spring-session/docs/current/api/org/springframework/session/MapSession.html
	   RemoteCache<String, MapSession> cache = (RemoteCache<String, MapSession>) cacheManager.getCache("sessions").getNativeCache();
	   MapSession mapSession = cache.get(session.getId());
//       return mapSession.getAttributeNames().toString();
       
		Map<String, String> value = new HashMap<String, String>();
		for (String key: mapSession.getAttributeNames()) {
			value.put(key, mapSession.getAttribute(key).toString());
		}
		return value.toString();
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