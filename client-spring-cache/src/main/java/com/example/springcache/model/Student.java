package com.example.springcache.model;

import java.io.Serializable;

public class Student implements Serializable{

	private static final long serialVersionUID = 6012152925385051324L;
	
	String id;
	String name;
	String email;

	public Student(String id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
//	public Student(String name, String email) {
//		super();
//		this.name = name;
//		this.email = email;
//	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", email=" + email + "]";
	}
	
}
