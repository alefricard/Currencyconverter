package com.ricardo.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.gson.JsonObject
import com.ricardo.currencyconverter.api.Endpoint
import com.ricardo.currencyconverter.databinding.ActivityMainBinding
import com.ricardo.currencyconverter.util.NetworkUtils
import retrofit2.Call
import retrofit2.Response




class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getCurrencies()

        binding.btConvert.setOnClickListener { convertMoney() }
    }

    fun convertMoney(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endponit = retrofitClient.create(Endpoint::class.java)

        endponit.getCurrencyRate(binding.spFrom.selectedItem.toString(),
            binding.spTo.selectedItem.toString()).enqueue(object: retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()?.find { it.key == binding.spTo.selectedItem.toString() }
                val rate : Double = data?.value.toString().toDouble()
                val conversion = binding.etValueFrom.text.toString().toDouble() * rate

                binding.tvResult.setText(conversion.toString())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Conexão falha!")
            }

        })


    }



    fun getCurrencies(){
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endponit = retrofitClient.create(Endpoint::class.java)

        endponit.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)

                }
                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                binding.spFrom.adapter = adapter
                binding.spTo.adapter = adapter

                binding.spFrom.setSelection(posBRL)
                binding.spTo.setSelection(posUSD)

            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Ocorreu uma falha de conexão")
            }
        })
    }
}