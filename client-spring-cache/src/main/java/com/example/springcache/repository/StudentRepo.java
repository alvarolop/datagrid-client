package com.example.springcache.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.springcache.controller.StudentNotFoundException;
import com.example.springcache.model.Student;

@Service
@CacheConfig(cacheNames="default")
public class StudentRepo {
	
    Logger log = LoggerFactory.getLogger(this.getClass());
    
	@CachePut(key = "#student.id")
	public Student putStudent(Student student) {
		log.info("---> Creating student with id '" + student.getId() + ": " + student.toString() + "'");
		try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return new Student(student.getId(),student.getName() , student.getEmail());
	}
	
	
	@Cacheable(key = "#id", unless="#result == null")
	public Student getStudentByID(String id) throws StudentNotFoundException {
		log.info("---> Student with id '" + id + "' not cached");
		throw new StudentNotFoundException("Student with id: " + id + " not found.");
	}
	
	@CacheEvict
	public void evictStudentByID(String id) {
		log.info("---> Evict student with id = " + id);
	}
	
	@CacheEvict(allEntries = true)
	public void evictStudents() {
		log.info("---> Evict All Entries.");
	}
	
}
