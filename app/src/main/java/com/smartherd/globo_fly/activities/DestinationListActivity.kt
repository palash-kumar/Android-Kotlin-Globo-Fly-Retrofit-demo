package com.smartherd.globo_fly.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.smartherd.globo_fly.R
import com.smartherd.globo_fly.helpers.DestinationAdapter
import com.smartherd.globo_fly.helpers.SampleData
import com.smartherd.globo_fly.activities.DestinationListActivity
import com.smartherd.globo_fly.models.Destination
import com.smartherd.globo_fly.services.DestinationService
import com.smartherd.globo_fly.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_destiny_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationListActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_destiny_list)

		setSupportActionBar(toolbar)
		toolbar.title = title

		fab.setOnClickListener {
			val intent = Intent(this@DestinationListActivity, DestinationCreateActivity::class.java)
			startActivity(intent)
		}
	}

	override fun onResume() {
		super.onResume()

		loadDestinations()
	}

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
}
