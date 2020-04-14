# TemanKondangan Backend
The backend system of TemanKondangan Apps

* Using PostgreSQL

Run Docker Locally

> You can use your local postgres, no need to run Postgres container. 
>
> And make sure you've run the Postgres on your local machine.

1. Build image docker
    ```
    docker build --tag {your_image_name}:1.0 .
    ```

2. Create container
    ```
    docker container create --name {your_container_name} 
   -e DB_URL=jdbc:postgresql://localhost:5432/postgres 
   -e DB_USERNAME={your_db_username} 
   -e DB_PASSWORD={your_db_password}
   -p 8181:8181 {your_image_name}:1.0 
    ```
3. Run the container
    ```
   docker container start {your_container_name}
    ```
