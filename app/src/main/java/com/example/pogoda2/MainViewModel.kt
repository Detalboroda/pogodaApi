package com.example.pogoda2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pogoda2.adapters.WeatherModel

class MainViewModel: ViewModel() {
    //класс где будут переменные которые будут обновляться
    val liveDataCurrent = MutableLiveData <WeatherModel>() // сюда будем записывать информацию верхней карточки приложения
    val liveDataList = MutableLiveData<List<WeatherModel>>() //сюда будем записывать расшитую информацию списком
// с погодой по каждому дню
}