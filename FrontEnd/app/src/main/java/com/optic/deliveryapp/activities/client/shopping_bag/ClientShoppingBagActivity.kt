package com.optic.deliveryapp.activities.client.shopping_bag

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.optic.deliveryapp.R
import com.optic.deliveryapp.activities.client.address.create.ClientAddressCreateActivity
import com.optic.deliveryapp.activities.client.address.list.ClientAddressListActivity
import com.optic.deliveryapp.adapters.ShoppingBagAdapter
import com.optic.deliveryapp.models.Product
import com.optic.deliveryapp.utils.SharedPref

class ClientShoppingBagActivity : AppCompatActivity() {

    var recyclerViewShoppingBag: RecyclerView? = null
    var textViewTotal: TextView? = null
    var buttonNext: Button? = null
    var toolbar: Toolbar? = null

    var adapter: ShoppingBagAdapter? = null
    var sharedPref: SharedPref? = null
    var gson = Gson()
    var selectedProducts = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_shopping_bag)

        sharedPref = SharedPref(this)

        recyclerViewShoppingBag = findViewById(R.id.recyclerview_shopping_bag)
        textViewTotal = findViewById(R.id.textview_total)
        buttonNext = findViewById(R.id.btn_next)
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar?.title = "Tu orden"

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.navigationIcon?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)

        recyclerViewShoppingBag?.layoutManager = LinearLayoutManager(this)

        getProductsFromSharedPref()

        buttonNext?.setOnClickListener {goToAddressList()}
    }

    private fun goToAddressList(){
        val i = Intent(this, ClientAddressListActivity::class.java)
        startActivity(i)
    }

    fun setTotal(total: Double){
        textViewTotal?.text = "S/.${total}"
    }

    private fun getProductsFromSharedPref(){
        if(!sharedPref?.getData("order").isNullOrBlank()){
            val type = object: TypeToken<ArrayList<Product>>(){}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"),type)

            adapter = ShoppingBagAdapter(this, selectedProducts)
            recyclerViewShoppingBag?.adapter = adapter
        }
    }
}