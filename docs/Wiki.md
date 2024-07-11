

# Phase 4

## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.

## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.

![EA Diagram](https://github.com/isel-leic-ls/2223-2-LEIC41N-G08/blob/main/docs/tasksDb/LS-EADiagram.png)

### Physical Model ###

The physical model of the database is available in ([Domain Schema](https://github.com/isel-leic-ls/2223-2-LEIC41N-G08/blob/main/src/main/sql/domainSchema.sql)).

## Software Organization

### Open-API Specification ###

([YAML file containing the Open-API Specification](https://github.com/isel-leic-ls/2223-2-LEIC41N-G08/blob/main/docs/TaskManegentAPI-1.0.0-resolved.yaml))

### Request Details
* To achieve authentication any user must do the register first to obtain a token that they will use at cookies to preserve the current session with authentication. 
	* If by any means the user isn't authenticated the server replies with an 401 HTTP Code.
		* All the API methods require Bearer Auth except for user Registration and retrieving of User Details.
* Registered users only need to log in to update their cookie status and stay authenticated.
*  The cookie's states are not being saved in the frontend.
* We also make use of a High Order Function in all the Controller Methods for code simplification reasons and to reduce code repetition . It basically encapsulates the running code block inside a try and catch so that we can make use of our exception handler.

* The Request Parameters have their Format validated inside their Services Class, but we check if they are or not in the Request Handler of the given Controller.

* We updated all the getAPI methods, that returned a sequence of elements in the past, to now support paging.

### Connection Management
 
* Connections to a database are created using a Connection String, which contains all the necessary Information for the Connection, such as the server name, database name and user credentials.
* Once a connection is established, it can be used to execute SQL commands against the database. 
  * Connections are a scarce resource, and we reuse them as much as possible because Opening and Closing connections repeatedly can have a significant impact on performance.

* When it comes to transactions, the connection is associated with JDBI Handler.
  * When a connection is associated with a transaction scope, any SQL commands executed against that connection will be part of the transaction. 
    * If the transaction fails, the connection will automatically be rolled back, and any changes made to the database will be undone.

* We use Transactions on the Service Domain to help to ensure data consistency, prevent data corruption, prevent race conditions, and provide an "all-or-nothing" guarantee all database operations happening at inside a Service. 
  * This makes the service more reliable and helps to ensure the data remains accurate and consistent.

### Single Page Application
* On this project we built a Single Page Application to provide a Web User Interface to our methods operations 
	* Our Single Page Implementation is based upon an internal Router instead of a simple Single Page Application implementation with just some method handlers.
	* We created a Domain Specific Language, so that we could produce HTML views with code for the given method handles.
	* We created register view to regist a new user and the login view to have access to the personal account.
	
### Data Access

* We have Interfaces to define the contracts that we wanted at Repository Level with Intended Operations.
  * Then we have a JDBI Implementation of the Interface that retrieves all information from the Database implemented on Postgres.
  * We also developed a Data Storage module in Memory following the Interface so that the application could be used without a direct connection to a Postgres Server.


### Error Handling/Processing

* As it was mentioned above, we make use of our personal Exception Handler, it's called Errors, and it Implements the Exception Class.
* When we envision certain operation can result in an Exception we throw it without any problems as soon as possible so that the hole service method can make its rollback taking use of the Transaction Manager and that the User be notified with an answer that explains what went wrong with the given request or the server if that's the case, it is all captured by our try and catch block in the Exception Handler Function in the Controller Utils file.


## Critical Evaluation

### Functionalities not Concluded and Identified Defects
* Missing Single Page Application tests
* 
