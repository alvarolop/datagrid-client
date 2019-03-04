package com.example.springcache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.springcache.model.Student;
import com.example.springcache.repository.StudentRepo;

@RestController
public class StudentController {
	
	@Autowired
	StudentRepo studentRepo;
	
	@Autowired
	CacheManager cacheManager;
	
	@PostMapping(path = "/student", consumes = "application/json")
	public Student postStudent(@RequestBody Student student) {
		return studentRepo.putStudent(student);
	}
	
	@GetMapping(path = "/student/{id}")
	public Student findStudentById(@PathVariable String id) {
	    try {
	    	return studentRepo.getStudentByID(id);
	    } catch (StudentNotFoundException ex) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
	    }
	}
	
	@DeleteMapping(path = "/student/{id}")
	public void evictStudentById(@PathVariable String id) {
		studentRepo.evictStudentByID(id);
	}
	
	@DeleteMapping(path = "/student")
	public void evictStudents() {
		studentRepo.evictStudents();
	}
}
