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

#### Setting up our application Globo-Fly
Application overview:
In this application there is a simple homescreen. Which will contanin button and description. The button will navigate to another activity which will load a list of several destination's from server through REST call. By clicking any of the item from the list will view the details. A user can also add or update information from another activity.
> HIGHLIGHTS: 
> * Load List
> * View item info
> * Add Item to the list
> * Update Information
> * Delete Item

##### Project Setup
Create a new kotlin project.
Add dependencies:
    * Add Recycler view
    * Add Card view

Now *right click* on the *app* goto *Module Settings* then go to *Dependencies*
![Dependency window](https://github.com/palash-kumar/Android-Kotlin-Globo-Fly-Retrofit-demo/blob/master/md-images/adding-dependencies-1.png "Dependency window")

click on **+** to add new dependency to the project and add the following dependencies:
**com.android.support:design**
![Dependency Support:design](https://github.com/palash-kumar/Android-Kotlin-Globo-Fly-Retrofit-demo/blob/master/md-images/com.android.support-design.png "Dependency Support:design")

>NOTE: Dependencies are added to the project can be verified from **Gradle Scripts -> build.gradle** file.

Adding retrofit to the project from dependency. 

**implementation 'com.squareup.retrofit2:retrofit:2.9.0'**
**implementation 'com.squareup.retrofit2:converter-gson:2.9.0'**

In the Application end we have to complete 3 steps to make *Retrofit* work in collaboration with server. These three steps are:
1. Interface: It will contain various functions which will map to the end point URL's of our web service
2. Service Builder: Next step is to creating a service which will help us to call the functions created in interface **#1**
3. Activity: As a last step in the activity we have to initialize the service **#2** and than call the functions of the interface **#1**. Eg: destinationService.getDestination().

![Retrofit Flow](https://github.com/palash-kumar/Android-Kotlin-Globo-Fly-Retrofit-demo/blob/master/md-images/retrofit-flow.png "Retrofit Flow")

> NOTE: Add the following lines while developing the app at: Android O (--min-api 26)

```java
//build.gradle 
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
#### Defining Web Service Interface and Service Builder

Create new package named **Services** and create new **Kotlin** file *DestinationService* as **Interface**.
The purpose of this interface is to define functions which will map to our web service endpoint URLs. Then we will create a function in *DestinationService*
```java
fun getDestinationList(): Call<List<Destination>>
```

Now our next step is to declare to which URL our function function will be mapped to
```java
    @GET("destination")
    fun getDestinationList(): Call<List<Destination>>
```
AS we can see in our code we are using **GET**, because in our *server.js* the very first endpoint URL is **app.get('/destination', function(req, res){...})**.
Now we have fnished instantiating our interface class, but it's still useless unless we instantiate a class to implent our *interface*. So now we will create another *kotlin* file as **object** and name it **ServiceBuilder**.
```java
object ServiceBuilder {
}
```
Now we will declare our server address:
```java
object ServiceBuilder {
    // This will be our server address
    private const val URL = "10.0.2.2:9000"
}
```
> Though we know that our server is running locally **@127.0.0.1:900** but we have declared **10.0.2.2:900** that's because we will be running our app on emulator so we the address **10.0.2.2:900** will automatically converted to **@127.0.0.1:900**.

As we have declared our Server or Host address, now it's time to declare our *OkHttp* client

```java
object ServiceBuilder {

    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "10.0.2.2:9000"

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
}
```
This client will help us to create the *Retrofit* builder, Therefore we will instantiate our *Retrofit* builder:
```java
object ServiceBuilder {

    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "10.0.2.2:9000"

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()

    // Create Retrofit builder and pass the URLas base url.
    private val builder = Retrofit.Builder().baseUrl(URL)
}
```
Than we will integrate *GsonConverterFactory* with our Retrofit so that we can convert **json** into destination objects and back&Forth and attach our client *okHttp*. Now creation of our builder is complete.
```java
object ServiceBuilder {

    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "10.0.2.2:9000"

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()

    // Create Retrofit builder
    private val builder = Retrofit.Builder().baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(okHttp.build())
}
```

Now we will initiate our *Retrofit*
```java
    //Create Retrofit Instance
    private val retrofit : Retrofit = builder.build()
```
As we have initiated our retrofit, we will create a function **buildService** that will accept a generic class.
```java
fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
```
Now the question is what will be the *serviceType* in the parameter of *buildService(ServiceType: Class<T>)*. The answer is as a parameter in this function we will pass the class which will implements the interface that we have recently defined *DestinationService* hence using the instance of the class that we will create here *we can call the functions* that we have defined in **DestinationService** interface such as 8getDestinationList()*. 

Now we are all set *Interface* and *Service Builder*.

#### Request and receive from Activity
In this section we will modify our code in **DestinationListActivity** to use our *Interface* for requesting and receiving response from server or host.
```java
// DestinationListActivity.kt
    private fun loadDestinations() {

        // To be replaced by retrofit code
		destiny_recycler_view.adapter = DestinationAdapter(SampleData.DESTINATIONS)
    }
```
updated to :
```java
    private fun loadDestinations() {

        // To be replaced by retrofit code
		//destiny_recycler_view.adapter = DestinationAdapter(SampleData.DESTINATIONS)

		val destinationService : DestinationService = ServiceBuilder.buildService(DestinationService::class.java)

        val requestcCall: Call<List<Destination>> = destinationService.getDestinationList()
    }
```
Here we have used *buildService* function from **ServiceBuilder** then passed in the *Interface* of **DestinationService**. So the function *buildService* will return the instance of the class that implements the interface, in our case it is *DestinationService*. Than we have called *getDestinationList()*, using the object reference of *destinationService*, which is declared in our interface. So the function, *getDestinationList()*, will return the **Call** object which will be respponsible to fetch the list of destinations from the server *(getDestinationList(): Call<List<Destination>>)* and get the result in *requestCall* which is a type of **Call<List<Destination>>**.

Than we will use *requestCall* object to make a network call **Asynchronously**, for which we have used the method *enque()*, which expects an anonymous innerclass object that implements the callBack Interface. But here we will not define the anonymous inner Class and use the interface and use lambda expression. Now we have to impor **CallBack** which is of **retrofit.Callback** or **retrofit.Callback**; Mostly *CallBack* is imported automatically. Than we will have to override two functions: *onFailuare* and *onResponse*. So our code will look like:
```java
private fun loadDestinations() {

        // To be replaced by retrofit code
		//destiny_recycler_view.adapter = DestinationAdapter(SampleData.DESTINATIONS)

		val destinationService : DestinationService = ServiceBuilder.buildService(DestinationService::class.java)

		val requestcCall: Call<List<Destination>> =  destinationService.getDestinationList()

		requestcCall.enqueue(object : Callback<List<Destination>>{

			override fun onResponse(
				call: Call<List<Destination>>,
				response: Response<List<Destination>>
			) {
				TODO("Not yet implemented")
			}

			override fun onFailure(call: Call<List<Destination>>, t: Throwable) {
				TODO("Not yet implemented")
			}

		})
    }
```

> Call : An invocation of a Retrofit method that sends a request to a webserver and returns a response. Each call yields its own HTTP request and response pair.
> enqueue(Callback<T> callback) : Asynchronously send the request and notify {@code callback} of its response or if an error occurred talking to the server, creating the request, or processing the response.

We will use the following function to check if our call was successful or not
```java
if(response.isSuccessful){
					
}
```
Than using the the same response object we can get body which contains destination lsit from our server:
```java
if(response.isSuccessful){
	val destinationList: List<Destination> = response.body()!!	

    destiny_recycler_view.adapter = DestinationAdapter(destinationList)
}
```

Complete codeblock: 
```java
private fun loadDestinations() {

        // To be replaced by retrofit code
		//destiny_recycler_view.adapter = DestinationAdapter(SampleData.DESTINATIONS)

		val destinationService : DestinationService = ServiceBuilder.buildService(DestinationService::class.java)

		val requestcCall: Call<List<Destination>> =  destinationService.getDestinationList()

		requestcCall.enqueue(object : Callback<List<Destination>>{

			override fun onResponse(
				call: Call<List<Destination>>,
				response: Response<List<Destination>>
			) {
				if(response.isSuccessful){
                    val destinationList: List<Destination> = response.body()!!	

                    destiny_recycler_view.adapter = DestinationAdapter(destinationList)
                }
			}

			override fun onFailure(call: Call<List<Destination>>, t: Throwable) {
				TODO("Not yet implemented")
			}

		})
    }
```

> Key Points:
> * First get the reference of **Service** in this case *destinationService*
> * Next, Use the object reference to call the required function from thre **service** layer and save the result to *requestCall* variable
> * Then use the requestCall to perform the network operation. 
> * Then the response will be received in either of the function **onResponse** or **onFailure**
> * IF any exception occurs of fails to request to any end-point try add **android:usesCleartextTraffic="true"** in **AndroidManifest.xml** in *<application android:usesCleartextTraffic="true">*

### Response and Error Handling
* How to check if the response is *Success*
* What makes a response a *Failuare*

Checking the response code:
```java
if(response.isSuccessful){
    // When a response code is between 200 ~ 299 it returns true.
    val destinationList: List<Destination> = response.body()!!

    Log.i("\nPrinting Destination List ","Call Is Success")
    Log.i("Printing Destination List ","destinationList: "+destinationList)
    //Toast.makeText(this, destinationList, Toast.LENGTH_LONG).show();
    //destiny_recycler_view.adapter = DestinationAdapter(destinationList)
}else if(response.code() == 401){
    // This is code specific so it will only execute when the code is 401
    Toast.makeText(this@WelcomeActivity, "Your Session has Expired!!!", Toast.LENGTH_LONG).show();
} else{// Application-Level Failuare
    // This code will lbe call for Status code - 300, 400 & 500
    Toast.makeText(this@WelcomeActivity, "Failed to Retrieve Items!!!", Toast.LENGTH_LONG).show();
}
```
The above code is execute at application level once the response is received from the server. But there remains another case where exception can occure if there is error while connecting to network, server or making any request; for such cases *onFailuare* function is invoked
```java
// This code isinvoked in case of Network Error or Establishing connection with server
// or Error Creating Http Request or Error Processing Http Response
override fun onFailure(call: Call<List<Destination>>, t: Throwable) {
    Log.i("\nPrinting Destination List ","Call Is Failed")
    Log.i("\nThrowable ",t.toString())
}
```

### Logging: Log Request and Response
This section will explain how to log the request and responses using logcat. As logging is importanat to identify any issues that occurs during request or response; most of the time in development we can see that we face some unexpected issues while running our application; therefore to debug and fix the issues properly it is necessary to identify the exact source of the issue. 

> Logging makes use of the concept called *Interceptor*, which is a powerful feature of **Retrofit** and **OkHttp**

To implement use *Logging* in our application we have to do following steps:
```java
//build.gradle
implementation 'com.squareup.okhttp3:logging-interceptor:4.7.2'
```

When implementing logger we have to specify the level. There are 4 levels of Logger:
    * BASIC - which will help to monitor the request type such as : GET, POST, PUT, DELETE, URL, Size of request and response, and Status code
    * BODY - Helps to monitor everything that included in HEADERS, BODY, and additionally Request and Response Body
    * HEADERS - Will help us to monitor everything in BASIC, additionally Headers.
    * NONE - Apply no Logging

 Than we have to modify our **service** layer or classes as following:
 Old code:
 ```java
    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "http://10.0.2.2:9000/"

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()

    // Create Retrofit builder
    private val builder : Retrofit.Builder = Retrofit.Builder().baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(okHttp.build())

    //Create Retrofit Instance
    private val retrofit : Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
```

Modified code:
```java
    // This will be our server address, chnage it before launching it to play-store
    private const val URL = "http://10.0.2.2:9000/"

    // Create Logger
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // Create OkHttp Client
    private val okHttp : OkHttpClient.Builder = OkHttpClient.Builder()
                                                .addInterceptor(logger)// Added for Intercept and logging

    // Create Retrofit builder
    private val builder : Retrofit.Builder = Retrofit.Builder().baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(okHttp.build())

    //Create Retrofit Instance
    private val retrofit : Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
```
Now we have implemented our logger, now each and every request made by the app will be logged in **Logcat** in *Android Studio*.
>NOTE: If you want to filter the requests specifically than in the seacrh of the *Logcat* simple search for *okHttp*. alphabet case doesn't matter.


