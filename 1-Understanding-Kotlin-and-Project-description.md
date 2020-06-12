### Client, WebServices, HTTP & Retrofit
Ref: [ Smartherd-YouTube](https://www.youtube.com/watch?v=7YTAsUrLrck&list=PLlxmoA0rQ-LzEmWs4T99j2w6VnaQVGEtR&index=3)

**Client's** are the devices on which an app runs. eg Laptop, Mobiles, arduino etc. In this case Mobile devices specifically Android devices are our client.

**WebServices** are applications which are hosted in server to serve different purposes. Eg. When trying to Login the login credentials are sent to server for authenticating by the app hosted on the server. Here authentication is a webservice which is provided by the app which is hosted in the server.

Client connects, requests and responses to and from server for services through **HTTP protocol**. 

In Android application we will be using Retrofit instead of using raw HTTPUrlConnection client used in Java or Volly client in Android. 

#### Popular HTTP Libraries
**OkHttp** Library handles http connections at a low level. It requires too many lines of codes to achieve a single request.

**HTTPUrlConnection** Library is built on top of *OkHttp* and it handles requests with more abstructions. Means it requires less lines of codes than OkHttp to achieve a single request. Let's assume that *OkHttp* requires 10 lines of codes whereas *HttpUrlConnection* achieves same connection with 7 lines of codes.

**Retrofit** Library is also built on top of *OkHttp* but more abstracted than *HTTPUrlConnection*. for eg. A request in *HTTPUrlConnection* can be achieved with 7 lines of codes, but in *Retrofit* same can be achieved with mere 3~4 lines of codes.

**Volly** Is another highlevel library which is similar to *Retrofit*, but it has it's own limitations.

> NOTE: All of the http Libraries uses *Background* threads therefore they do not interfare with *main* thread which runs the user interface, and works Asynchronously. 


##### Con of HTTPUrlConnection
1. Poor Readability and les expressive 
2. Requires Lots of boilerplate codes eg: Byte arrays, stream readers
3. No Built in support of parsing JSON response
4. Background threads are required to be managed manually to perform multiple asynchronous requests, and poor resource management

Above mentoned cons are improved in *Volly* and *Retrofit*.

##### Con of Volly
Though Volly has solved the issues of *HTTPUrlConnection* but it has some con's of it's own compared to *Retrofit* library
1. Limited RET specific features
2. Poor Authentication features
3. has a small support community

##### Retrofit
The *Retrofit* library solves all of the problems that comes with *HTTPUrlConnection* and *Volly*. 
1. It has large active community which makes troubleshooting easier
2. The codes are more expressive and abstract
3. It manages resources efficiently such as
    a. Background threads
    b. Async calls & queues
    c. Automatic **JSON** parsing with **GSON** library.
    d. Automatic error handling callbacks
    e. Built-in User Authentication support

### Understanding RESTful Web Services
A web service is RESTful when it provides *stateless operations* to manage data using different *HTTP methods* and *structured URLs*.

##### HTTP Fundamentals
    * HTTP Request
    * HTTP Response
    * Status Code

**HTTP** stands for *Hyper Text Transfer Protocol* which is an application level protocol for *distributed, collaborative, and hypermedia information systems*
    * **GET** method is usually used to retrieve data froom server.
    * **POST** method is usually used to add data to server.
    * **PUT** method is used to replace object in server
    * **PATCH** is used to update resouce or parts of resource
    * **DELETE** is used to delete a resource

| Status Code Range | Represents    |
| ------------------|:-------------:|
| 100's             | Informational |
| 200's             | Success       |
| 300's             | Redirects     |
| 400's             | Client Error  |
| 500's             | Server Error  |

Currently we are familier with two types of services **SOAP** and **WSDL** which uses **XML** and is heavy.

| HTTP Method   | Constraint URL| Web Service Operation  |
| ------------- |:-------------:| ----------------------:|
| GET           | xyz.com/user  | Fetch User             |
| POST          | xyz.com/user  | Add User               |
| PUT           | xyz.com/user  | Update User            |
| DELETE        | xyz.com/user  | Delete User            |

### Installing NODE with npm
For this series of tutorial we will be using Node. As it is a light weight javascript library. 

Download **nodejs** from here : [ nodejs](https://nodejs.org/en/download/)

##### This instruction set is for Linux
1. Download *nodejs* of **Linux Binaries (x64)** version
2. Copy to */opt/*
3. Unzip the tar file **tar xf node-v12.18.0-linux-x64.tar.xz**
4. open *profile* file
```shell
    nano ~/.profile
```
5. Add the following lines to *profile*
```shell
    # NodeJS
    export NODEJS_HOME=/opt/node-v12.18.0-linux-x64/bin
    export PATH=$NODEJS_HOME:$PATH
```
6. Refresh *profile*
```shell
. ~/.profile
```
7. Add the same lines @5 to *bashrc*
```shell
    nano ~/.bashrc
```
8. Now check the installation
```shell
    [aryan-pc opt]# node -v
    v12.18.0
    [aryan-pc opt]# npm version
    {
    npm: '6.14.4',
    ares: '1.16.0',
    brotli: '1.0.7',
    cldr: '37.0',
    http_parser: '2.9.3',
    icu: '67.1',
    llhttp: '2.0.4',
    modules: '72',
    napi: '6',
    nghttp2: '1.41.0',
    node: '12.18.0',
    openssl: '1.1.1g',
    tz: '2019c',
    unicode: '13.0',
    uv: '1.37.0',
    v8: '7.8.279.23-node.37',
    zlib: '1.2.11'
    }

```

### Setting up Web Services with NODE
To set up webservices and for sake of practice download the following files *package.json* & *server.js* from [ here](https://gist.github.com/smartherd/a229384cc3c40ebe3a2db10298c4b91b)
Move the files to seperate directory, in this case we have used same name as *Smartherd*'s tutorial *MyNodeApp*.

Now got to the directory *MyNodeApp* 
```shell
cd MyNodeApp
```

Run **npm install** to install all the required dependencies.
```shell
[aryan-pc MyNodeApp]# npm install
npm notice created a lockfile as package-lock.json. You should commit this file.
added 50 packages from 37 contributors and audited 50 packages in 2.776s
found 0 vulnerabilities
```

Now start the server using *node server.js*
```shell
[aryan-pc MyNodeApp]# node server.js 
Server running at http://127.0.0.1:9000/
```

### Demo application Globo-Fly-Retrofit
This demo application is built through followup tutorial by *smartherd* during practice [ GloboFly-Retrofit-Demo](https://github.com/smartherd/GloboFly-Retrofit-Demo) and is replicated with the name [ Globo-Fly-Retrofit-Demo](https://github.com/smartherd/GloboFly-Retrofit-Demo). This demo app will have following operations:

| HTTP Method   | URL end point | Web Service Operation  |
| ------------- |:-------------:| ----------------------:|
| GET           | /messages     | Get Promotional message|
| GET           | /destination  | Get List of saved destinations |
| GET           | /destination/id  | Get one destinations from list |
| POST          | /destination  | Add New destinations |
| PUT           | /destination/id | Update a destinations |
| DELETE        | /destination/id  | Delete a destinations |