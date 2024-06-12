package com.example.happypets.ui.citas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CitasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is citas Fragment"
    }
    val text: LiveData<String> = _text
}