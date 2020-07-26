## Spring-boot Microservices Open-Id Authentication with KeyCloak

This example project show how to integrate KeyCloak with Spring-boot Micro services. \
Also the inter service communication is done between the services those are secured.

###Prerequisites

1. ``docker`` should be installed in the environment
2. ``Maven`` should be installed in the environment
3. ``8001, 8002, 8083`` ports should be free.

### Setup KeyCloak Service

1. run following command to run keycloak container in the local machine
```docker run -d -p 8083:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:10.0.2```

### Configure KeyCloak Service
1. Login to the KeyCloak Service using following credentials and url
    ``http://localhost:8083/``
    ``UserName: admin, Password: admin``

2. Create Realm as ``Company`` and select the realm from the dropdown.

3. Create employee client using ``KeyCloakConfig/employee.json``,
   ``Company Realm > Clients > Add Client > Import > employee.json > Save``

4. Create department client using ``KeyCloakConfig/deparment.json``,
   ``Company Realm > Clients > Add Client > Import > deparment.json > Save``
   
5. Create ``App-admin`` role and configure it. ``Company Realm > Roles > Add Role`` name it as ``App-admin``
and save it.

6. Create ``App-user`` role and configure it. ``Company Realm > Roles > Add Role`` name it as ``App-user``
and save it.

7. Add user roles from the above services and add them to the ``App-User`` Realm Role. ``Company Realm > Roles > App-user > Edit > Composite roles(ON) > Composite Roles``
   type ``employee`` and select ``user`` from the list and add the ``user`` role from ``department`` service also.

8. Add user roles from the above services and add them to the ``App-Admin`` Realm Role. ``Company Realm > Roles > App-user > Edit > Composite roles(ON) > Composite Roles``
   type ``employee`` and select ``admin`` from the list and add the ``admin`` role from ``department`` service also.
   
9. Create user privileged user with as ``employee1``. ``Company Realm > Users > Add User``
```$xslt  
  User Name: employee1
  User Enabled: ON
  Email Verified: ON
``` 
and ``Save`` it.
  
10. Update credentials, ``Company Realm > Users > employee1`` update passwords as ``12345`` ``tempory> OFF`` and save it.

11. Update credentials, ``Company Realm > Users > employee1 > Role Mapping > Realm Roles`` and select ``app-user``

12. Create admin privileged user with as ``employee2``. ``Company Realm > Users > Add User``
```$xslt
  User Name: employee2
  User Enabled: ON
  Email Verified: ON
``` 
and ``Save`` it.
  
13. Update credentials, ``Company Realm > Users > employee2`` update passwords as ``12345`` ``tempory> OFF`` and save it.

14. Update credentials, ``Company Realm > Users > employee2 > Role Mapping > Realm Roles`` and select ``app-admin``

15. Assign service account roles. In this example we are going to call department service from employee service. Therefore we need to configure Service account roles to employee service client.
``Company Realm > Clients > employee > Service Account Roles `` search for ``department`` and add ``user, admin`` roles from it \
to the employee client.

16. Set-up protocol mapper for ``employee-service``. ``Company Realm > clients > employee > Mappers > Create`` with \
following spec.

```$xslt
Name: User Name
Mapper Type: User Property
property: username
Token Claim Name: user_name
Claim Json Type: String
Add to Id Token: ON
Add to Access Token: ON
Add to User info: ON
```
and ``Save`` it.

### Update Spring-boot micro services

``keycloak.credentials.secret`` value should be updated with in the each service ``application.properties`` file.

1. update ``employee`` service.  Navigate to ``company Realm > Clients > employee > Credentials > Secret`` and copy value \
and update the value in ``application.properties`` file in the ``employee`` service.

2. update ``department`` service.  Navigate to ``company Realm > Clients > department > Credentials > Secret`` and copy value \
and update the value in ``application.properties`` file in the ``department`` service.

### Start Services

1. Start ``employee-micro-service`` \
``mvn spring-boot:run -f employee-service/pom.xml``

2. Start ``department-micro-service`` \
``mvn spring-boot:run -f department-service/pom.xml``

3. Obtain OAuth2 token for user. 

### Create Requests

```$xslt
curl --location --request POST 'http://localhost:8083/auth/realms/company/protocol/openid-connect/token' \
   --header 'grant_type: password' \
   --header 'client_id: springboot-microservice' \
   --header 'client_secret: 82a20a81-c54f-4737-8167-dbdf178f7213' \
   --header 'username: employee1' \
   --header 'password: mypassword' \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --header 'Cookie: AUTH_SESSION_ID_LEGACY=b3304add-997d-4cc9-863d-5460afbf5a71.669a5cad2966' \
   --data-urlencode 'grant_type=password' \
   --data-urlencode 'client_id=employee' \
   --data-urlencode 'client_secret=5cc8bc2b-9df1-4021-8851-35c3d8f23008' \
   --data-urlencode 'username=employee1' \
   --data-urlencode 'password=12345'
```
* To obtains ``client_secret`` value, `` Company Realm > Clients > employee > credentials > Secret``

and copy the ``access_token`` value.

Scenario 1: Make Request as a user directly to a micro service.

```
curl -X GET 'http://localhost:8002/department/user' \
  --header 'Authorization: bearer <access_token>'
```

Scenario 2: Make Request as user to a micro service which refers to another micro service.

```
curl -X GET 'http://localhost:8001/employee/user' \
  --header 'Authorization: bearer <access_token>'
```

* This scenario simulates the call from ``employee`` service to ``department`` service.

###References

https://medium.com/@bcarunmail/accessing-secure-rest-api-using-spring-oauth2resttemplate-ef18377e2e05

https://medium.com/devops-dudes/securing-spring-boot-rest-apis-with-keycloak-1d760b2004e

###Special Thanks
'Dinuth De Zoysa' & 'Arun B Chandrasekaran' for their Medium Articles.


 