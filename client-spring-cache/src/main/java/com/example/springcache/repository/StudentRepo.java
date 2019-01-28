package com.example.springcache.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.springcache.domain.Student;

@Service
@CacheConfig(cacheNames="student")
public class StudentRepo {
	
    Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Cacheable(key = "#id")
	public Student getStudentByID(String id) {
		log.info("---> Loading student with id '" + id + "'");
		try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Student(id,"Sajal" , String.valueOf(id));
		
	}
	
	@CacheEvict
	public void evictStudentByID(String id) {
		log.info("---> Evict student with id = " + id);
	}
	
	@CacheEvict(allEntries = true)
	public void evictStudents() {
		log.info("---> Evict All Entries.");
	}
	
//	@Cacheable
//	public Student getStudents() {
//		log.info("---> Loading student with id '" + id + "'");
//		try {
//			Thread.sleep(1000*5);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return new Student(id,"Sajal" , String.valueOf(id));
//		
//	}
	
}
