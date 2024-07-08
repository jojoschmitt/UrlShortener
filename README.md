# RESTful URL Shortener

This project comprises a RESTful URL shortener service based on a Spring Boot application.

Users can map a short URL to an original URL. Browsing mapped short URLs will redirect the client to the original URL. URL mappings can be deleted or set for a specified duration.

# Setup
## Requirements
* Docker

## Installation
The URL shortener is a **Maven** project using **Spring Boot 3.3.1** based on **Java 22**.

This installation guide assumes that it is known how to start a Spring Boot application. If this is not the case, please reach out to us.

The Spring Boot application uses a MySQL database to store data. Hence, it is necessary to provide a database to the application.
For this purpose, this repository includes a **dockerized MySQL** database including the database administration tool **phpMyAdmin**.

This installation guide assumes that installing docker, which is required to run the dockerized database, is also known.

# Usage
## Database
To start the database server navigate to `<repo-root>/MySQL Server`
and execute `docker compose up`.

phpMyAdmin will be available on `http://localhost:8888`.

Username: **root**\
Password: **superSecure**

## RESTful API
After launching the Spring Boot application, the RESTful URL shortener supports the following functionalities:

### Shorten
**Description**\
Given an arbitrary URL, the shorten endpoint will generate a new short URL. The application also provides a service that will redirect the client from the shortened URL to the original URL.
The representation of the short URL can optionally be defined by the user as an alphanumeric string.
Optionally, the user can also define a TTL (in minutes) to induce an expiration time for the short URL. 

**URL**: `http://localhost:8080/api/url/shorten` \
**HTTP Method**: `POST`\
**Content-Type**: `application/json` \
**Request Parameters**: `["originalUrl", "shortUrlRep", "ttl"]`\
**Response Parameters**: `["shortUrl", "errorMessage"]`

**Example Request Body**: `{"originalUrl":"https://google.com", "shortUrlRep":"gg", "ttl":"60"}`\
Creates a short URL (`http://localhost:8080/api/url/gg`) that lasts for 1 hour and redirects the client to the original URL (`https://google.com`).

**Example Response Body**: `{"shortUrl":"http://localhost:8080/api/url/gg"}`

### Delete
**Description**\
Deletes a short URL given its representation.

**URL**: `http://localhost:8080/api/url/<shortUrlRep>` \
**HTTP Method**: `DELETE`