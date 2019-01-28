package com.example.springcache.controller;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcache.domain.Student;
import com.example.springcache.repository.StudentRepo;

@RestController("student")
public class StudentController {
	
	@Autowired
	StudentRepo studentRepo;
	
	@Autowired
	CacheManager cacheManager;
	
	@GetMapping("/student/{id}")
	public Student findStudentById(@PathVariable String id) {
		return studentRepo.getStudentByID(id);
	}
	
	@GetMapping("/student/{id}/evict")
	public void evictStudentById(@PathVariable String id) {
		studentRepo.evictStudentByID(id);
	}
	
	@GetMapping("/student/evict")
	public void evictStudents() {
		studentRepo.evictStudents();
	}
	
	@GetMapping("/student/allEntries")
	public List<Student> getStudentEntries() {
		@SuppressWarnings("unchecked")
		RemoteCache<String, Student> cache = (RemoteCache<String, Student>) cacheManager.getCache("student").getNativeCache();
		List<Student> value = new ArrayList<Student>();
		for (Object key: cache.keySet()) {
		    value.add(studentRepo.getStudentByID((String) key));
		  }
		return value;
	}
	
	@GetMapping("/student/allKeys")
	public List<String> getStudentKeys() {
		@SuppressWarnings("unchecked")
		RemoteCache<String, Student> cache = (RemoteCache<String, Student>) cacheManager.getCache("student").getNativeCache();
		List<String> value = new ArrayList<String>();
		for (Object key: cache.keySet()) {
		    value.add(String.valueOf(key));
		  }
		return value;
	}
	
}
