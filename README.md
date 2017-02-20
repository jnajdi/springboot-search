## Properties (db, etc.)
application-default.properties

## Lg4j properties
logback-spring.xml


## Building

    mvn clean package
    
    1. Generate archive for a specific site and user. 
    	java -jar -Dspring.profiles.active=default target/osp-matrix-loader-1.0.jar b8f508d6-1bf3-467c-b519-03213224a8f1  qsteen
	java -jar target/scholar-migration-tool-1.0.jar b8f508d6-1bf3-467c-b519-03213224a8f1 vratcliffe

    	USER-IDENTIFIER: Either the user_id or user pid
    
    
 
 

Testing: 
https://preprod.scholar.vt.edu/portal/site/b8f508d6-1bf3-467c-b519-03213224a8f1
Organizer: vanessa - linkousk
Participant: jlstine - lharwood	

