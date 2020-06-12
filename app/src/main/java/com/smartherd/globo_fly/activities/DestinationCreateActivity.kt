package com.smartherd.globo_fly.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.smartherd.globo_fly.R
import com.smartherd.globo_fly.helpers.SampleData
import com.smartherd.globo_fly.models.Destination
import com.smartherd.globo_fly.services.DestinationService
import com.smartherd.globo_fly.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationCreateActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_create)

		setSupportActionBar(toolbar)
		val context = this

		// Show the Up button in the action bar.
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
	}
}
