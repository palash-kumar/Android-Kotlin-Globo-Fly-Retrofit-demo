### Retrieve Data from web  Service
This section will cover the followings:
    * Understanding Request Parameters
    * Using Path Parameters
    * Using Query Parameters
    * Handling Multiple Query Parameters using QueryMap
    * Requesting resource from different web service as well as from alternate URL

#### Understanding Request Parameters
Request parameters are generally part of an URL where there is ? mark and key paired values along with the URL. The URL parameters can be of several types depending on the client's request or as required while developing the appication:
Types of URL with parameter can be:
    1. www.globofly.com/destination/12  // parameter as path variable
    2. www.globofly.com/destination?id=12   // parameter value as number
    3. www.globofly.com/destination?name=toyota // paramter values as string
    4. www.globofly.com/destination?id=12&name=toyota // multiple parameters

A URL can be constructed in two ways:
    1. Path Parameters
    2. Query Parameters

##### Using Path Parameters
To use path parameters first we have to create a function in our service layer which will use Path parameter:
```java
    @GET("destination/{id}")
    fun getDestination(@Path("id")id: Int):Call<Destination>
```
Using *{id}* along with the parameter enables to make the path variable dynamic. To set value to *{id}* we have to set function parameter as such **@Path("id")id: Int** and set the type of the variable. Now whenever we will make a request the URL will be like *www.globofly.com/destination/12*

Currently in our code **DestinationDetailActivity.kt** the *loadDetails(id)* loads the details of the destination selected from *destinationList*.
```java
private fun loadDetails(id: Int) {

    // To be replaced by retrofit code
    val destination = SampleData.getDestinationById(id)

    destination?.let {
        et_city.setText(destination.city)
        et_description.setText(destination.description)
        et_country.setText(destination.country)

        collapsing_toolbar.title = destination.city
    }
}
```
Now we will modify our code in *loadDetails(id)* to call and load the details from the server:
```java
private fun loadDetails(id: Int) {

    val destinationService : DestinationService = ServiceBuilder.buildService(DestinationService::class.java)

    val requestCall = destinationService.getDestination(id)

    requestCall.enqueue(object : retrofit2.Callback<Destination>{
        override fun onResponse(call: Call<Destination>, response: Response<Destination>) {
            if(response.isSuccessful){
                val destination: Destination? = response.body()

                destination?.let {
                    et_city.setText(destination.city)
                    et_description.setText(destination.description)
                    et_country.setText(destination.country)

                    collapsing_toolbar.title = destination.city
                }
            }else{
                Toast.makeText(this@DestinationDetailActivity, "No Item found with ID = "+id+"!!!", Toast.LENGTH_LONG).show();
            }
        }

        override fun onFailure(call: Call<Destination>, t: Throwable) {
            Toast.makeText(this@DestinationDetailActivity, "Failed to Retrieve Items!!!", Toast.LENGTH_LONG).show();
        }


    })
}
```
> NOTE: When filtering a List based on single type of value *Eg: Finding all the managers in a company* it will return a List only for manager, only when the parameter is sent as *Manager* like *getManagers("Manager")*, **ELSE** if *null* is sent instead of sending *Manager* as parameter it *Retrofit* will ignore the parameter and return complete list.

When **Filtering** the codes/Functions in the *service* and *activity* classes should be like: 
```java
    @GET("destination")
    fun getDestinationList(@Query("country") country : String): Call<List<Destination>>
```
> So for the above function the URL will look like *http://xyz.com/destination?country=country_name*
> Now if **null** is sent instead of sending a **country_name** than the URL will be like *http://xyz.com/destination* and as a result it will send full list.

#### Using Query Map
**@QueryMap** is used when we will have to use more than 2 or 3 parameters for our query. Without *@QueryMap* we will have to write and maintain all the parameters at the *service* and *activity* layer. When using multiple parameters for query we can implement it in two ways:
    1. **@Query** : Use *@Query* and write all the query parameters.
    2. **@QueryMap** : Use *@QueryMap* to send a *hashmap* unsing single parameter. 

An Example for each case has been provided below:
1. **@Query**
```java
// code in SERVICE layer would be like this
@GET("destination)
fun getDestinationList(@Query("Country") country : String?, @Query("count") count: String?): Call<List<Destination>>
```
and
```java
// code in ACTIVITY layer would be like this
val requestcCall: Call<List<Destination>> =  destinationService.getDestinationList("country_name", "1")
```
2. **@QueryMap**
```java
// code in SERVICE layer would be like this
@GET("destination)
fun getDestinationList(@QueryMap filter: HashMap<String, String>): Call<List<Destination>>
```
and
```java
// code in ACTIVITY layer would be like this
val filter = HashMap<String, String>()
filter["country"]= "country_name"
filter["count"]="2"
val requestcCall: Call<List<Destination>> =  destinationService.getDestinationList(filter)
```
The resutant URL for both **1** & **2** will be *http://xyz.com/destination?country=country_name&count=2*

#### Retrieve data from alternate URL or Another Server
Generally we use a single server for our application. Now there might arise a situation where one have to retrieve data from another server. For such case in *Retrofit* there is **Alternate URL**, using which we can instruct retrofit to ignore our base url and retrieve data from another server, or use another URL as base URL. To Implement the use of *Alternate URL* we will create another local server with different port. As our primary server is running at *http://127.0.0.1:9000* so we will make our ssecondary server run at *http://127.0.0.1:7000*
**STEPS:**
1. Copy the *server.js* and *package.json* to another directory named *secondServer*
2. Open directory in *terminal* and write
```shell
npm install
```
3. Now open *server.js* and edit it we will modify some of the codes. 
    * remove all the end-points except "/" & "/messages"
    * change the port to 7000

Now for our apllication in the *WelcomeActivity* we will load the message dyamically. 
It's better to create a new *Interface* layer, for our application we will name it *MessageService.kt*
```java
    @GET
    fun getMessages(@Url secondServer: String?): Call<String>
```
As we can see from the function that we have not declared any endpoint url, because here we will be providing the whole url as we will be using *Alternate Base URL* using annotation **@Url** instead of calling the main base URL.

We will call *getMessages()* function from our *WelcomeActivity* to display the message from second server.
```java
// WelcomeActivity.kt
val messageService = ServiceBuilder.buildService(MessageService::class.java)

val requestCall: Call<String> = messageService.getMessages("http://10.0.2.2:7000/messages")

requestCall.enqueue(object : Callback<String>{
    override fun onResponse(call: Call<String>, response: Response<String>) {

        if(response.isSuccessful){
            val msg : String?= response.body()
            msg?.let {
                message.text = msg
            }
        }
    }

    override fun onFailure(call: Call<String>, t: Throwable) {
        Toast.makeText(this@WelcomeActivity, "Connection Failed", Toast.LENGTH_LONG).show();
        Log.i("\nThrowable from http://10.0.2.2:7000/",t.toString())
    }

})
```
This time in *WelcomeActivity.kt* we can have initialized *ServiceBuilder* with *MessageService* instead of *DestinationService*. This is because we will be calling function from another *Interface* layer. 
In the next line we are making a requesst call in a usual way, but this time we hve provided a complete URL along with the End-Point *http://10.0.2.2:7000/messages*, as we are using another server to fetch our data.

Along with this we have completed requesting or fetching data for our application and show then in our activity.

### Sending Data to The Web Service
1. Sending Data to Server
    * In request Bodt of HTTP Request
2. Two Types of Data Format in HTTP
    * JSON
    * Form URL Encoded
3. Complete CRUDE Operation
    * POST
    * PUT
    * DELETE
4. Sending Data Using HTTP Headers
    * Modify Headers Statically & Dynamically

#### POST: Create New Resource 
##### Sending Data to Web Service
In This section we will learn to add new data to our database. We will *POST* our data as JSON. 
![POST as JSON](https://github.com/palash-kumar/Msg-Share-App/blob/master/md-images/Post_as_JSON.png "POST as JSON")

In our *server.js* we have a *POST* function * **app.post('/destination', function(req, res)** to add new data to the existing List of Destinations. 

Now to send our new information/object to the server we will create a function in our *DstinationService.kt* Interface
```java
@POST("destination")
fun addDestination(@Body newDestination: Destination): Call<Destination>
```
In the code we can see that we have anotated our *newDestination* parameter with **@BODY** annotation. So in Runtime *Retrofit* with the help of *Gson-converter* will convert this object into *JSON* object, and in return we will receive a *Call* object which is the type of *Destination*. Than to use the function as *POST* we will anotate the function with **@POST** annotation along with the end-point. 

Now we will use the *POST* function in our *DestinationCreateActivity.kt*
```java
btn_add.setOnClickListener {
    val newDestination = Destination()
    newDestination.city = et_city.text.toString()
    newDestination.description = et_description.text.toString()
    newDestination.country = et_country.text.toString()

    // To be replaced by retrofit code
    //SampleData.addDestination(newDestination)
    //finish() // Move back to DestinationListActivity

    val destinationService = ServiceBuilder.buildService(DestinationService::class.java)

    val requestCall: Call<Destination> = destinationService.addDestination(newDestination)

    requestCall.enqueue(object : Callback<Destination>{
        override fun onResponse(call: Call<Destination>, response: Response<Destination>) {

            if(response.isSuccessful){
                finish() // Move back to DestinationListActivity
                val newAddedDestination : Destination? = response.body() // If required use it or ignore it
                Toast.makeText(context, "Item Successfully added !!!", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Failed to add Item !!!", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<Destination>, t: Throwable) {
            Toast.makeText(context, "Failed. Something went wrong !!!", Toast.LENGTH_LONG).show()
        }

    })
}
```
In the code to make a *POST* request everything is same as *GET* request. In network call some general steps are required to be followed. 
1. We have created our destination service using *ServiceBuilder.buildService(DestinationService :: class.java)
2. We have prepared a requestCall with the function and required data, in this case **newDestination**
3. We have initiated a background network call using *enque()* and requested *CallBack* with expecting object *Destination*
4. Override the *onResponse* and *onFailuare* functions.
5. If the response is successfull:
    * we have called *finish()* to return us to the *DestinationListActivity* as we have pressed the *add* button to get to the form to create a new destination.
    * We have received the response body which we were expecting @3 - CallBack<Destination>
    * Showed a notification *Toast* to show the user a message.
6. Once the *finish()* function is called to return to the revious activity, we have to tell our *DestinationListActivity* to perform some action which is **onResume()**
```java
// DestinationListActivity.kt
override fun onResume() {
    super.onResume()

    loadDestinations()
}
```

#### PUT: Update / Replace Existing Resource
In this part we will focus on sending data to the server using **FormUrlEncoded Format**.
![Form URL Encoded Format](https://github.com/palash-kumar/Msg-Share-App/blob/master/md-images/FormUrlEncodedFormat.png "Form URL Encoded Format")

In our *server.js* we have a *PUT* function  **app.put('/destination/:id', function(req, res)** to update data to the existing List of Destinations. 
```javascript
// Update a Destination 
app.put('/destination/:id', function(req, res) {
    var destination;
    for (var i = 0; i < destinations.length; i++) {
        if (destinations[i].id == req.params.id) {
            destinations[i].city = req.body.city;
            destinations[i].country = req.body.country;
            destinations[i].description = req.body.description;
            destination = destinations[i];
        }
    }

    res.end(JSON.stringify(destination));
})
```
In the code we can see that we will be using *JSON* in our Http Response not in the form of *FormUrlEncoded*, but when we will be requesting we will request in *FormUrlEncoded* format.

Now we will create a function in our *DestinationService.kt* named **updateDestination(): Call<Destination>**
```java
@FormUrlEncoded
@PUT("destination/{id}")
fun updateDestination(@Path("id") id: Int,
                        @Field("city") city: String,
                        @Field("description") desc: String,
                        @Field("country") country: String
): Call<Destination>
```
Please note that we have used **@FormUrlEncoded** annotation to enable the request to have body of *FormUrlEncoded*, and within the function we have to declare all of the Fields with **@Field** annotation that we want to include as *FormUrlEncoded* format.

> NOTE: The field's name should be same as the name mentioned in serverside where requested for parameters.

As we have done preparing our function at interface; And now we will implement the function in out **DestinationDetailActivity.kt**. Therefore our code will be like:
```java
// DestinationDetailsActivity.kt
private fun initUpdateButton(id: Int) {

    btn_update.setOnClickListener {

        val city = et_city.text.toString()
        val description = et_description.text.toString()
        val country = et_country.text.toString()

        val destinationService = ServiceBuilder.buildService(DestinationService::class.java)

        val requestCall: Call<Destination> = destinationService.updateDestination(id, city, description, country)

        requestCall.enqueue(object : Callback<Destination>{
            override fun onResponse(call: Call<Destination>, response: Response<Destination>) {
                if (response.isSuccessful){

                    finish() // Move back to DestinationListActivity
                    val updatedDestination : Destination? = response.body() // If required use it or ignore it
                    Toast.makeText(context, "Item Successfully Updated !!!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Failed to Update Item !!!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Destination>, t: Throwable) {
                Toast.makeText(context, "Failed. Something went wrong !!!", Toast.LENGTH_LONG).show()
            }

        })
    }
}
```

#### DELETE: Delete Existing Resource from server
In this part we will focus on Deleting data from server. In our server side we have the following code to delete data from server:
// Delete a Destination 
app.delete('/destination/:id', function(req, res) {
    for (var i = 0; i < destinations.length; i++) {
        if (destinations[i].id == req.params.id) {
            destinations.splice(i, 1);
            res.status(204).end(JSON.stringify(destinations[i]));
        }
    }
});

So to delete an item frokm server our code at *DestinationService* will be like:
```java
@DELETE("destination/{id}")
fun deleteDestination(@Path("id") id : Int): Call<Unit>
```
In the above function we have used **Call<Unit>** instead of **Call<Destination>**; as we already know that **Call<T>**, where *T* represents object, Integer, Long, String or anything that we receive in response; represets what we require or requesting as response from server. Therefore we are using *Unit*, in **Kotlin** *Unit* represents *Void* as we mention in java. Eg:
In Kotlin
```java
    fun deleteDestination(): Call<Unit>
```
is same as this:
```java
public void deleteDestination(){
    // codes
}
``` 

In our *DestinationDetailActivity* we have impleted the funtion to delete a destination from our existing list of Destinations.
```java
private fun initUpdateButton(id: Int) {

    btn_update.setOnClickListener {

        val city = et_city.text.toString()
        val description = et_description.text.toString()
        val country = et_country.text.toString()

        val destinationService = ServiceBuilder.buildService(DestinationService::class.java)

        val requestCall: Call<Destination> = destinationService.updateDestination(id, city, description, country)

        requestCall.enqueue(object : Callback<Destination>{
            override fun onResponse(call: Call<Destination>, response: Response<Destination>) {
                if (response.isSuccessful){

                    finish() // Move back to DestinationListActivity
                    val updatedDestination : Destination? = response.body() // If required use it or ignore it
                    Toast.makeText(context, "Item Successfully Updated !!!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Failed to Update Item !!!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Destination>, t: Throwable) {
                Toast.makeText(context, "Failed. Something went wrong !!!", Toast.LENGTH_LONG).show()
            }

        })
    }
}
```

#### Working with HTTP Headers
The Headers represents the meta data of the HTTP Network call. And it is a part of every HTTP Request & Response.
| Request Headers   | Response Headers |
| ------------- |:-------------:|
| User-Agent: Chrome/5.0 | User-Agent: Chrome/5.0 |
| Accept-Language: en-US | Accept-Language: en-US |
| If-Modified-Since: 09/08/2020 | If-Modified-Since: 09/08/2020 |
These are the genereic headers which is always present in a response or request HTTP Network call. Beside these parameters some other parameters can also be adde to the header. In this section we will learn to use *Headers* with our HTTP Requests. We will use our *getDestinationList()* as an example:
```java
@Headers("x-device-type: Android", "x-foo: bar")
@GET("destination")
fun getDestinationList(): Call<List<Destination>>
```
In the above code we have added custom headers with our request.

> NOTE: It can be seen that **x** has been added in the beginning of each of the parameter name. And it is a standerd Http header syntax to add a prefix **x-** in the beginning of every header name.

Now we will see how to add headers dynamically with our HTTP requests. 
```java
@Headers("x-device-type: Android", "x-foo: bar")
@GET("destination")
fun getDestinationList(@Header("Accept-Language") language: String?, @Header("x-App-Name") appNam: String): Call<List<Destination>>
```

#### Adding Headers Application wide using INTERCEPTOR
Normally in an application we require header to pass some data. But defining the common headers to every function is not feasible. Therefore we will be using our servicebuilder to apply the common headers application wide.
This is how we have to configure our interceptor:
```java
/// ServiceBuilder.kt
// Creating a custom Interceptor to apply headers application wide
val headerInterceptor: Interceptor = object : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        request = request.newBuilder()
                    .addHeader("x-device-type", Build.DEVICE)
                    .addHeader("Accept-Language", Locale.getDefault().language)
                    .build()

        val response: Response = chain.proceed(request)

        return  response
    }
} 
```
Than we will have to modify our **okHttp** in the following way:
```java
// Create OkHttp Client
private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
                                            .addInterceptor(headerInterceptor)// Added for adding Headers Application wide
                                            .addInterceptor(logger)// Added for Intercept and logging
```
> Note: We have to add our **headerInterceptor** before the **logger** interceptor so that we can see the request in our *LogCat*

#### Request Timeouts
One issue we will face while working with HTTP requests is *Slow Connection* which can be caused due to slow connectivity, wireless issue or any other type of network issues, it can also be caused due to serverside request processing which may be the result of slow underlying dependencies or some other server side related issues.

By default all the retrofit timeouts is **10 sec** ; So if we make a network call and it takes more than 10 sec than **on Failure()** function will be executed. There are four types of timeout:
    1. **connectTimeout** is the length retrofit will wait to actually establish a network connection to the server.
    2. **readTimeout** is the length that retrofit will wait to receive data once the connection has been established.
    3. **writeTimeout** is the time that retrofit will wait between write attempts to the server once the connection has been established
    4. **callTimeout** allows us to set timeout for the entire network operation 
To implement the *Timeout* here are the codes here we will be implementing **callTimeout**:
```java
// Create OkHttp Client
private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
                                            .callTimeout(15, TimeUnit.SECONDS)
                                            .addInterceptor(headerInterceptor)// Added for adding Headers Application wide
                                            .addInterceptor(logger)// Added for Intercept and logging
```
Generally in an application by default timeout is set to **10 sec**, here we have increased the timeout to **15 sec** which is not a wise decession, it's better to set the timout to **5 sec** in deployed applications.

#### Cancelling HTTP Request
To cancel an ongoing request we simply have to call a cancel function in an activity, or it can be said that add the **requestCall.cancel()** to the activity where one is require a cancel button to cancel a network request. Eg: If any file has started downloading due to mistakenly click of a button, and the downlad has to be cancelled. 

