package com.example.pogoda2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pogoda2.MainViewModel
import com.example.pogoda2.adapters.WeatherModel
import com.example.pogoda2.adapters.WheatherAdapter
import com.example.pogoda2.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {
private lateinit var binding:FragmentHoursBinding // подключили байдинг
private lateinit var adapter1:WheatherAdapter //создали локальную переменную чтобы иметь доступ к WheatherAdapter
  private val model: MainViewModel by activityViewModels( ) //иницилизировали MainViewModel чтобы через
// апсервер обновлять нижнюю карточку

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentHoursBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView() //иницилизировали RcView
        model.liveDataCurrent.observe(viewLifecycleOwner){
            adapter1.submitList(getHoursList(it))
            }
        }


    private fun initRcView() = with(binding){
        //это мысоздали функцию гдеполучили доступ RcView который находится в fragment_hours.xml
        rcView.layoutManager = LinearLayoutManager(activity)
    //layoutManager нужен чтобы получить доступ к тому как показывать список
    //LinearLayoutManager по умолчанию показывает список вертикально
        adapter1 = WheatherAdapter(null) //иницилизировали наш адаптер
        rcView.adapter = adapter1 //это означает что у rcView адаптером будет adapter1
    // который наследуется от WheatherAdapter
    }
    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
    //тоесть мы берем уже имеющийся WeatherModel
    //и из val hours:String где хранятся данные списком передаем нужные данные
    // уже новые WeatherModel в список List, это прогноз погоды по часам,
    // каждый WeatherModel идет отдельно на каждый час, и мы берем наш массив из hours и прогоняем
    // через цикл по нашей модельке 24 раза (т.к. 24 часа)
        val hoursArray = JSONArray(wItem.hours) //переделываем hours:String в список hoursArray,
        // теперь это джейсон массив из array (списков)  объектов
        val list = ArrayList<WeatherModel>() //создаем список с классами WeatherModel куда будем все помещать
        for(i in 0 until hoursArray.length()){
           val item = WeatherModel(
               wItem.city,
               (hoursArray[i] as JSONObject).getString("time"),
               (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
               (hoursArray[i] as JSONObject).getString("temp_c"),
               "",
               "",
               (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
               ""
           )
            list.add(item) //добавляем нужные данные в список
        }
        return list //возвращаемся к переменной лист
    }


    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
            }
    }
