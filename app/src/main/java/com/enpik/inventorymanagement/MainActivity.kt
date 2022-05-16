package com.enpik.inventorymanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.*
import com.enpik.inventorymanagement.adapter.CustomAdapter
import com.enpik.inventorymanagement.controller.ApiController
import com.enpik.inventorymanagement.model.ValidationResponseModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiController = ApiController()

        val itemCode = findViewById<TextView>(R.id.itemCode) as EditText
        val itemName = findViewById<TextView>(R.id.itemName) as EditText
        val itemQuantity = findViewById<TextView>(R.id.itemQuantity) as EditText
        val itemManufacturer = findViewById<TextView>(R.id.itemManufacturer) as AutoCompleteTextView
        val successLabel = findViewById<TextView>(R.id.successLabel)
        val failureLabel = findViewById<TextView>(R.id.failureLabel)
        val saveButton = findViewById<TextView>(R.id.saveButton)
        val manufacturers = apiController.getManufacturers()
        val adapter = CustomAdapter(this, manufacturers)
        itemManufacturer.setAdapter(adapter)
        itemManufacturer.threshold = 0;
        itemCode.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })

        itemManufacturer.setOnClickListener {
            itemManufacturer.showDropDown()
        }

        itemManufacturer.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                itemManufacturer.showDropDown()
            }
        }

        var manufacturerId : Int = -1
        itemManufacturer.setOnItemClickListener { adapterView, view, i, l ->
            itemManufacturer.error = null
            itemManufacturer.setText(manufacturers.get(i).name)
            manufacturerId = manufacturers.get(i).id
        }

        saveButton.setOnClickListener {
            val validatedResponse = validateInputs(itemCode.text.toString(), itemName.text.toString(), itemQuantity.text.toString(), itemManufacturer.text.toString())
            if(validatedResponse.isValid){
                failureLabel.visibility = View.GONE
                successLabel.visibility = View.GONE
                var flag = false
                for (manufacture in manufacturers) {
                    if(itemManufacturer.text.toString().equals(manufacture.name)){
                            flag = true
                            manufacturerId = manufacture.id
                            break
                    }
                }
                var addItemResponse = ""
                if(flag){
                    addItemResponse = apiController.addItem(itemCode.text.toString(),itemName.text.toString(),manufacturerId,itemQuantity.text.toString().toInt())
                } else{
                        val manufecturerResponse = apiController.addManufacturer(itemManufacturer.text.toString())
                        if(manufecturerResponse.id == -1){
                            Toast.makeText(applicationContext,"Some error occured",Toast.LENGTH_SHORT).show()
                        } else{
                            manufacturers.add(manufecturerResponse)
                            adapter.add(manufecturerResponse)
                            adapter.notifyDataSetChanged()
                            addItemResponse = apiController.addItem(itemCode.text.toString(),itemName.text.toString(),manufecturerResponse.id,itemQuantity.text.toString().toInt())
                        }                        //save item
                }
                if(!addItemResponse.equals("") || addItemResponse.length>1){
                    failureLabel.text = addItemResponse
                    failureLabel.visibility = View.VISIBLE
                }
                else{
                    successLabel.text = itemCode.text.toString()+" Saved. Add another item?"
                    successLabel.visibility = View.VISIBLE
                }
            } else {
                failureLabel.text = validatedResponse.message
                failureLabel.visibility = View.VISIBLE
                successLabel.visibility = View.GONE
            }
        }
    }

    fun validateInputs(code : String, name : String, quantity : String , manufacturer : String) : ValidationResponseModel {
        val validationResponse = ValidationResponseModel(true, "");
        // Code Validation
        if(code.trim().length < 5 || code.trim().length > 20){
            validationResponse.isValid = false
            validationResponse.message = "Item Code Between 5-20 characters"
            return validationResponse
        }
        // Name Validation
        if(name.trim().length < 3 || name.trim().length > 256){
            validationResponse.isValid = false
            validationResponse.message = "Item Name Between 3-256 characters"
            return validationResponse
        }
        // Manufacturer validation
        if(manufacturer.trim().isEmpty()){
            validationResponse.isValid = false
            validationResponse.message = "Manufacturer can't be empty"
            return validationResponse
        }
        // Quantity Validation
        if(quantity.isEmpty()){
            validationResponse.isValid = false
            validationResponse.message = "Quantity can't be empty"
            return validationResponse
        }
        try {
            val parsedInt = quantity.toInt()
            if(parsedInt <= 0){
                validationResponse.isValid = false
                validationResponse.message = "Quantity can't be negative or zero"
                return validationResponse
            }
        } catch (e: Exception) {
            validationResponse.isValid = false
            validationResponse.message = "Invalid Quantity Value"
            return validationResponse
        }

        return validationResponse
    }
}