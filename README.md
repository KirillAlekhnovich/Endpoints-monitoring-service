# Endpoints monitoring service

The task is to create a REST API JSON Java microservice which allows you to monitor particular http/https URLs.

## Table of contents

* [Requirements](#Requirements)
* [Assignment](#Assignment)
* [Usage](#Usage)
    * [User entity](#User-entity)
      * [Create](#Create-user)
    * [Monitored Endpoint entity](#Monitored-Endpoint-entity)
      * [Create](#Create-endpoint)
      * [Get](#Get-endpoint)
      * [Update](#Update-endpoint)
      * [Delete](#Delete-endpoint)
    * [Result entity](#Result-entity)
      * [Get](#Get-result)
* [Postman Collection](#Postman)
* [Testing](#Testing)

## Requirements

* Java version: 17
* SpringBoot version: 2.7.1
* Prepared MySQL database. Check out [this instruction](DBGuide.md).

## Assignment

* You can find complete description of this task [here](ASSIGNMENT.md).

## Usage

### User entity

#### Create user

You are able to create users with POST query on `https://localhost:8080/users`. 
Your JSON should look like that:
```json
{
  "username" : "Applifting",
  "email" : "info@applifting.cz",
  "accessToken" : "93f39e2f-80de-4033-99ee-249d92736a25"
}
```

In order to create and modify endpoints and see results of the monitoring you need to be **authorized**. 
To do so, add new field in HTTP header called `Access-token` and insert your access token value.

### Monitored Endpoint entity

#### Create endpoint

Each user may have some endpoints he would like to monitor. 
To create them use this POST query on `https://localhost:8080/endpoints`:

```json
{
  "name" : "My repositories",
  "url" : "https://api.github.com/users/KirillAlekhnovich/repos",
  "monitoredInterval" : 30
}
```

Note that you are not allowed to enter date of creation or date of last check manually.
Additionally, monitored interval can't be less or equal to 0.

#### Get endpoint

To get all of your created endpoints use GET query on `https://localhost:8080/endpoints`. 
If you want to check one specific endpoint add `/{endpointId}` to the link above.

#### Update endpoint

Updating is just a PUT query on a same link(`https://localhost:8080/endpoints`).
JSON:

```json
{
  "name" : "Let's watch some YouTube",
  "url" : "https://youtu.be/dQw4w9WgXcQ"
}
```

All restrictions from create category are valid there (editing dates and entering invalid interval).
Moreover, you are not allowed to reassign the owner because it's illegal to force another user to monitor an endpoint.

#### Delete endpoint

In order to delete an endpoint you need to use DELETE query on `https://localhost:8080/endpoints/{endpointId}` 
where `endpointId` is the id of the endpoint that you would like to delete.

### Result entity

#### Get result

If you want to get one specific result use GET query on `https://localhost:8080/results/{resultId}`.

To get last 10 results of monitoring of an endpoint use GET query on `https://localhost:8080/results/last_endpoint_results/{endpointId}`.

## Postman

I have prepared some Postman requests to test functionality of the server. 

Postman collection: `https://www.postman.com/kirillalekhnovich/workspace/task-workspace/collection/18158241-bd4e8e03-2a0a-40d9-bf1d-3b704a2df7e7?action=share&creator=18158241`

I strongly suggest you to execute requests according to their order in the collection because some operations require id as path variable.
If you want to execute requests in your order, keep in mind that ids may differ.

## Testing

You can find controller and service tests in the corresponding [folder](src/test/java/com/applifting/task).
