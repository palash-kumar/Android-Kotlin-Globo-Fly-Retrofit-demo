package com.smartherd.globo_fly.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.smartherd.globo_fly.R
import com.smartherd.globo_fly.activities.DestinationListActivity
import com.smartherd.globo_fly.helpers.DestinationAdapter
import com.smartherd.globo_fly.models.Destination
import com.smartherd.globo_fly.services.DestinationService
import com.smartherd.globo_fly.services.MessageService
import com.smartherd.globo_fly.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_list.*
import kotlinx.android.synthetic.main.activity_welcome.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)

		// To be replaced by retrofit code
		// message.text = "Black Friday! Get 50% cash back on saving your first spot."

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



		buttonShowToast.setOnClickListener{
			Log.i("MainActivity", "Button was clicked");

			// Calling the toast from extenstion Extension,kt
			// Here we are not implementing the second parameter, as we have set a default value for the duration
			// If we wanr to change the duration whe can simply add Toast.LENGTH_LONG as a second parameter
			//showToast(resources.getString(R.string.buttonWasClicked))
			// Displaying a popup message for short length
			// To be replaced by retrofit code
			//destiny_recycler_view.adapter = DestinationAdapter(SampleData.DESTINATIONS)

			Toast.makeText(this, "Calling for API ", Toast.LENGTH_LONG).show();

			val destinationService : DestinationService = ServiceBuilder.buildService(
				DestinationService::class.java)

			val requestcCall: Call<List<Destination>> =  destinationService.getDestinationList()
			Log.i("\nRequesting call ","Requesting call--1")
			requestcCall.enqueue(object : Callback<List<Destination>> {

				override fun onResponse(
					call: Call<List<Destination>>,
					response: Response<List<Destination>>
				) {
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
				}

				// This code isinvoked in case of Network Error or Establishing connection with server
				// or Error Creating Http Request or Error Processing Http Response
				override fun onFailure(call: Call<List<Destination>>, t: Throwable) {
					Log.i("\nPrinting Destination List ","Call Is Failed")
					Log.i("\nThrowable ",t.toString())
				}

			})

		}
	}

	fun getStarted(view: View) {

		val intent = Intent(this, DestinationListActivity::class.java)
		startActivity(intent)
		finish()
	}
}
