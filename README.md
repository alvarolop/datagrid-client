Red Hat Data Grid Quickstarts for Spring Boot
=============================

## The official Red Hat Data Grid Quickstarts repository
This repository contains some complimentary examples of the officil RHDG Quickstarts repository. Please refer to the JBoss Developer repository for general examples of RHDG: https://github.com/jboss-developer/jboss-jdg-quickstarts.

## About these Quickstarts
The purpose of this repository is to show some examples of the many ways a Datagrid client can connect a remote Datagrid server using the Spring Boot project.


## Available Quickstarts
The repository contains four Spring Boot RHDG clients: client-spring-basic, client-spring-cache, client-spring-session, and client-spring-security. Each of them is a modification of the previous one that adds new functionality. 

| &nbsp; &nbsp; &nbsp; &nbsp; Step  &nbsp; &nbsp; &nbsp; &nbsp; | Quickstart   |                Example              |
|--------|------------------------|----------------------------------------------------------------------------------------------------------------------------|
| Step 1 | client-spring-basic    | Connects to the remote cache using the RemoteCacheManager class manually.               |
| Step 2 | client-spring-cache    | Connects to the remote cache using the Spring Cache project.                                    |
| Step 3 | client-spring-session  | Stores sessions and information about students in the remote cache using Spring Cache and Spring Session.         |
| Step 4 | client-spring-security | Adds a security layer using Spring Security and stores the information of the connection in the remote cache. Uses Spring Security, Spring Cache and Spring Session |




## Running the Quickstarts
Refer to the `README` file in each quickstart directory for instructions on building and running the quickstart.
