# IS-Project 

## Overview 
In this project, students will develop a web application that exposes web services and a client application that will consume these web services.

Students should write the applications using WebFlux. Next, we detail the requirements of these applications. Students may add additional functionality under professors’ guidance. 

## Reactive Server 

#### Students should create a server that will expose the following data via web services: 

- **Pet data, including**: 
    - Identifier 
    - Name 
    - Species 
    - Birth date 
    - Weight. 
- **Owner data, including**: 
    - Identifier 
    - Name 
    - Telephone Number

    The Pet and Owner entities have a one-to-many relationship, as a single owner may have many pets. 
 
The server is a legacy application with basic functionality that students must not try to enrich, to simplify client-side queries.

Server services are, therefore, limited to simple CRUD operations, according to the following list: 
- **Create Pet/Owner** 
- **Read specific Pet/Owner or all Pets/Owners** 
- **Update specific Pet/Owner**
- **Delete specific Pet/Owner (if the owner is not connected to another pet)**
 
The  server must  not  provide  a  service  with  all  the  data,  like,  owners  and all  the respective pets, names, and remaining details. 

Nonetheless, the server can provide a service  that  lists  the  identifiers  of  pets  given  an  owner  identifier.  This  has  some 
impact on the last queries ahead. 
 
Students must use logging on the server side.
 
## Client Using Reactive Code 

On  the  client  side,  students  should  collect  data  from  the  server  according  to  the following queries. Responses may go to different files and may run in parallel. 

For example, the output of query #1 may go to file resp1.txt, output of query #2 may go 
to file resp2.txt, etc. No interaction and no menu are necessary: 
 
1. Names and telephones of all Owners 
2. Total number of Pets. 
3. Total number of dogs. 
4. Total number of animals weighting more than 10 kg. Sort this list by ascending order of animal weight. 
5. Average and standard deviations of animal weights. 
6. The name of the eldest Pet. 
7. Average number of Pets per Owner, considering only owners with more than one animal. 
8. Name  of  Owner  and  number  of  respective  Pets,  sorted  by  this  number  in descending  order.  In  this  and  other  exercises,  students  should  minimize  the blocking points for their applications, e.g., by means of a block() call. 
9. The same as before but now with the names of all pets instead of simply the number. 
 
Note that most of the work should occur on the client, as mentioned 
before, on the limitations imposed on the server. Time matters.

Students should try to make these queries run as quickly as possible, 
by exploring all the data in the same query, or by taking advantage of threads. They are  allowed  to  introduce  delays  on  the  server  side  to  emulate  network  delays. 

Students are not allowed to copy the entire databases to the client or use some other technique that defeats the idea that the client is interacting with a legacy third-party server.

For example, any change to the server requirements should be discussed with 
the professors. 
 
At least one of the client-side queries should be able to tolerate network failures, by retrying  up  to  three  times  to  reconnect,  before  giving  up.

Students  may  create  a special  service  on  the  server  and  a  special  query  on  the  client,  outside  any  time control, to emulate this case. 
 
**Techniques not to use in the project**: 
- Using standard Java Streams. 
- Atomic Objects (unless there is a good reason for that). 
- Abuse of collectList() to make the posterior use of a standard for outside 
the flux. 
- Other approaches that circumvent the natural operation of Reactor core
