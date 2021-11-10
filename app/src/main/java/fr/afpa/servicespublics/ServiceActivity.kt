package fr.afpa.servicespublics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import fr.afpa.servicespublics.api.ClientApi
import fr.afpa.servicespublics.metier.CityJsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceActivity : AppCompatActivity() {

    var clientAPI = ClientApi()
    lateinit var input_cityName : TextView
    lateinit var input_cityCode : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        input_cityName = findViewById(R.id.ville_plain_text)
        input_cityCode = findViewById(R.id.code_postal_plain_text)
        val search_button = findViewById<Button>(R.id.bouton_recherche)
        search_button.setOnClickListener {
            launchResearch()
        }
    }

    fun launchResearch() {
        var cityCode: String
        val postalCode = input_cityCode.text.toString()
        val cityName = input_cityName.text.toString()
        val codeValid = checkCityCodeValidity(postalCode)
        if(codeValid)
            getCityCodeWithPostalCode(postalCode)
        else if(cityName.isNotEmpty()){
            cityCode = getCityCodeWithName(cityName)
        }
    }

    //On verifie si le code est de la bonne longueur et est composé de caractères numériques
    fun checkCityCodeValidity(code: String):Boolean {
        if(code.length!=5)
            return false

        for(char in code)
        {
          if(char.code <48 || char.code>57)
              return false
        }
        return true
    }

    fun getCityCodeWithName(cityName:String): String {
        var cityCode=""
        clientAPI.service.getCityCodeWithName(cityName).enqueue(object : Callback<List<CityJsonObject>> {

            override fun onResponse(call: Call<List<CityJsonObject>>, response: Response<List<CityJsonObject>>) {
                val jsonObject = response.body()

                jsonObject?.let {
                    displayPostalCode(jsonObject[0])
                    cityCode = jsonObject[0].code
                    Log.d("", "SUCCESS")
                }
            }
            override fun onFailure(call: Call<List<CityJsonObject>>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })

        return cityCode
    }

    fun getCityCodeWithPostalCode(postalCode: String) {
        var cityCode=""

        clientAPI.service.getCityCodeWithPostalCode(postalCode).enqueue(object : Callback<List<CityJsonObject>> {
            override fun onResponse(call: Call<List<CityJsonObject>>, response: Response<List<CityJsonObject>>) {
                val jsonObject = response.body()

                jsonObject?.let {
                    displayPostalCode(jsonObject[0])
                    cityCode = jsonObject[0].code
                    displayCityname(jsonObject[0])
                    Log.d("", "SUCCESS")
                }
            }
            override fun onFailure(call: Call<List<CityJsonObject>>, t: Throwable) {
                Log.e("REG", "Error : $t")
            }
        })
    }

    //TEST et DEBUG
    fun displayPostalCode(jsonObject: CityJsonObject){
        input_cityCode.text = jsonObject.code
    }

    fun displayCityname(jsonObject: CityJsonObject){
        input_cityName.text = jsonObject.nom
    }
}