# Getting Started

The recipe-manager service handles the management of recipes. The end points of the rest API hanles below operations:
1) Create/Add recipe.
2) Update recipe.
3) Remove recipe.
4) Find all recipes.
5) Search based on any combination of recipe model attributes.

# Tech stack

1) Java 17.0
2) Spring boot 3.1.2
3) Lombok
4) spotify  2.36.0
5) H2 database 2.1.214
6) Maven

# Design considerations/briefs

## Controller layer

The controller layer handles the end  points of the application. It provides end points for crud and search operations.
The presentation layer deals with DTO object to take input and represent output. The conversions happen through a custome model mapper.

## Service layer

The service layer interacts with the controller and persistence layer. The business logic of every end points are in service class.

## Persistence layer

A repository interface is created extending JPARepository. The crud methods are used in service classes. For search logic, criteria and
specifications are used. The search is all combined with and now, so any field can be combined with any other to search
The database is created at the start of application. In memory database is used . After the start of application, we can access the database in browser
with below url:
http://localhost:8080/h2-console/login.jsp

database url: jdbc:h2:file:./recipeManagerDB
username : sa
password: sa

## Model design

Recipe object is created with name, ingredients, food category, servings, search text. The food category is created as enum
to increase compile-time checking and one extra type "UNKNOWN" is added for demo and testing. The requirement says to have
search for "VEG" recipes. An enum is well suited for food type/category since these are pre-defined constants and will not be of many types
As per design, it is easy to add other food categories quite easily.
An Ingredient object is created and embedded to Recipe DAO as List, This mapping is @OneToMany. Since there is
not much focus on ingredient crud operations, no end points have been created for perforing crud on ingredient object.
We could have had list of strings to store ingredients. This can also be done via creating list of Ingredients object, this option levarages
with having future requirements such as ingredient quantity, category etc. So it is easily scalable.

## Search logic

In the requirement, it is mentioned that the end point can have one or more conditions. The specification with page request is creaated
to build the queries though criteria builder. Assuming that the application layer can interact with different databases in the future,
the native queries has not been chosen here( even though we have more control) .We can improve more on search if we have a particular search combination
criteria. At present, the search criteria combines with and() for every field. A search DTO is created to accommodate the search fields.

## Model logic

To retrieve all recipes, a RecipeResponseDTO has been created to accommodate the list of RecipeDTO and an extra field http status.
In this way it is easy to add any other field to display as output along with recipes. Only DAO layer is interacting with the database,
all DTO requests are converted to DAO to before saving to database and DAO are converted to DTOs for presentation layer. In this way we will have more control
over the input and output fields.

## cicd details

The cicd file is created using github action. This file is responsible for building creating, and pushing docker image
to docker hub. At present, this is only done for master branch.
It does maven build , docker build and push.

# Reference/Guides

### To format the application:

        mvn spotless::apply

### To clean build :

        mvn clean install

### To run application :

        mvn spring-boot:run

### To run with profile:

for prod:
mvn spring-boot:run -Dspring-boot.run.profiles=prod
for dev :
mvn spring-boot:run -Dspring-boot.run.profiles=dev

### To Swagger ui: http://localhost:8080/swagger-ui/index.html

### For docker

local build :
docker build -t dibakar721304/recipe-manager:v1.0 .
push:
docker push dibakar721304/recipe-manager:v1.0
To pull from docker hub : docker pull dibakar721304/recipe-manager:v1.0
To running docker image:  docker run -p 8080:8080 dibakar721304/recipe-manager:v1.0

### The high level design is in ..recipe-manager/design-uml-diagrams/High-level-design.jpg

# End points usage:

via postman:

## Login API :

POST http://localhost:8080/auth/login
request body:
{
"userName": "admin",
"password":"admin"
}
Copy the token value from response. Example response:
{
"token": "xxx.xxx.xxx"
}
To execute below mentioned apis, select Bearer token option from Authorization tab and
paste the token value in token field on the right. Now we can execute below apis with token authorization:

### POST http://localhost:8080/recipes

Request body:

{
"name":"testRecipe",
"foodCategory": "VEG",
"servings": 4,
"ingredients":
[
   {
"ingredientName": "pepper"
},
{
"ingredientName": "sugar"
}],
"instructions":"Test instruction for  recipe"
}

### GET http://localhost:8080/recipes

### PUT http://localhost:8080/recipes/update/1

Request body:
{
"name":"updatedRecipe",
"foodCategory": "UNKOWN",
"servings": 1,
"ingredients":
[
   {
"ingredientName": "salt3"
},
{
"ingredientName": "sugar3"
}],
"instructions":"updated instruction for recipe5"
}

### GET http://localhost:8080/recipes/id/1

### DELETE http://localhost:8080/recipes/delete/1

### GET http://localhost:8080/recipes/search?foodCategory=VEG&servings=4&includedIngredients=pepper&excludedIngredients=pepper&searchTextInInstructions=stir&name=testRecipe

