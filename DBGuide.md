# Database guide

Describes how to create local database and use it with this project.

### Steps

1) Install MySQL according to the [official manual](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/). 
2) Open the terminal and type `create database "db_name";`, where `"db_name"` is database name on your choice.
3) Change the [properties file](src/main/resources/application.properties) in order to connect your local database to the server.
4) To start using our database type `use "db_name";`
5) In order to fill it with information you have to start the server.
6) To see tables type `show tables;`
7) To see columns of a particular table type `show columns from "table_name";`
8) To see what's stored in a database by entity type `select * from "table_name";` or `select id from "table_name";` if you want to see id only etc.
9) To delete local database from your computer type `drop database "db_name";`



select id, date_of_check, returned_http_status_code, monitored_endpoint_id from result;