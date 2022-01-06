# Security application

## Run it locally
To run the application locally:
- If first time, execute on project root: `./gradlew bootJar && docker-compose up -d --build`
- If not, execute on project root: `docker-compose up -d`

Docker compose will expose:
- Application on port 8080
- Postgres database on port 5432
- Mail Server
  - SMTP service on port 1025
  - Dashboard on port 1080 (check http://localhost:1080)