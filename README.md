# Demo Webapp
Just a small Java webapp where you can login with a username/password that is then validated against a MySQL database. Built using pure java from scratch, no frameworks.

**See it live:** <https://16.58.74.62/>  
(your browser will warn you the certificate is invalid because it's self signed)

## How to build it locally
You will need
- Eclipse
- Tomcat server 11.0
- JDK at least 21
- a local mysql server

Once the project is imported into eclipse, update config.properties for your local database.  
The gradle tests should then be able to run. There is also a gradle task to produce a .war to deploy to your tomcat server.
