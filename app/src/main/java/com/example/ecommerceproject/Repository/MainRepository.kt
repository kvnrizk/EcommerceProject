package com.example.ecommerceproject.Repository

import androidx.compose.animation.core.snap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerceproject.Model.CategoryModel
import com.example.ecommerceproject.Model.ItemsModel
import com.example.ecommerceproject.Model.SliderModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainRepository {
    private val firebaseDatabase=FirebaseDatabase.getInstance()

    fun loadBanner():LiveData<MutableList<SliderModel>>{
        val listData=MutableLiveData<MutableList<SliderModel>>()
        val ref=firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(SliderModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        val listData=MutableLiveData<MutableList<CategoryModel>>()
        val ref=firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadPopular():LiveData<MutableList<ItemsModel>>{
        val listData=MutableLiveData<MutableList<ItemsModel>>()
        val ref=firebaseDatabase.getReference("Items")
        val query:Query=ref.orderByChild("showRecommended").equalTo(true)
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null){
                        lists.add(list)
                    }
                }
                listData.value = lists // hi
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }

    fun loadFilterd(id:String):LiveData<MutableList<ItemsModel>>{
        val listData=MutableLiveData<MutableList<ItemsModel>>()
        val ref=firebaseDatabase.getReference("Items")
        val query:Query=ref.orderByChild("categoryId").equalTo(id)
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null){
                        lists.add(list)
                    }
                }
                listData.value = lists // hi
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return listData
    }
}