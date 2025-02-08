package com.example.ecommerceproject.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerceproject.Model.CategoryModel
import com.example.ecommerceproject.Model.ItemsModel
import com.example.ecommerceproject.Model.SliderModel
import com.example.ecommerceproject.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadBanner(): LiveData<MutableList<SliderModel>> {
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        return repository.loadPopular()
    }

    fun loadFilterd(id:String): LiveData<MutableList<ItemsModel>>{
        return repository.loadFilterd(id)
    }

}