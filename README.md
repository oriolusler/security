# Security application

##Run it locally
To run the application locally:
- If first time, execute: `docker-compose up --build -d`
- If not, execute: `docker-compose up -d`

The application will run on a docker container with port 8080 exposed

##Endpoints (+examples)
### **Register example:**
- Type: POST
- Url: localhost:8080/api/auth/register
- Body:
```
{
    "email": "email@online.com",
    "password": "123456789",
    "name": "name",
    "phone": "+34666112233",
    "roles": [
        "ROLE_USER"
    ]
}
```

### **Login**
- Type: POST
- Url: localhost:8080/api/auth/login
- Body:
```
{
    "email": "email@online.com",
    "password": "123456789"
}
```