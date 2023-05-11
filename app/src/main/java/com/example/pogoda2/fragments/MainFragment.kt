package com.example.pogoda2.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pogoda2.MainViewModel
import com.example.pogoda2.adapters.WeatherModel
import com.example.pogoda2.adapters.vpAdapter
import com.example.pogoda2.databinding.FragmentMainBinding
import com.example.pogoda2.dialogManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "f3e34b7bc6c243c996345742231404" //создали константу с ключом
class MainFragment : Fragment() {
    private lateinit var fLocationClient:FusedLocationProviderClient //с помощью него получаем расположение
    private val  frList = listOf( // создали списки неизменяемые
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf(
        "Hours",
        "Days"
    )
private lateinit var binding: FragmentMainBinding
private lateinit var pLauncher: ActivityResultLauncher<String>
//это наш запрос который будет спрашивать у пользователя разрешение
private val model:MainViewModel by activityViewModels( ) //иницилизировали MainViewModel чтобы через
// апсервер обновлять верхнюю карточку

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //когда все вью созданы
        super.onViewCreated(view, savedInstanceState)
        checkPermission()//запускаем созданную ниже функцию permissionListener
        init()// запускаем функцию  init
        updateCurrentCard() // заускаем функцию которая будетобновлять данные
    }

    override fun onResume() { // добавили onResume чтобы если мы вурнулись в приложение запускался checkLocation()
        super.onResume()
        checkLocation()
    }
    private fun init()=with(binding){
        //создали функцию для подключения vpAdapter,
// with(binding) позволяет распространитьбайдинг на все чтобы не писать его каждый раз в этой функции
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = vpAdapter(activity as FragmentActivity, frList) //тут требуется передать две вещи
        //activity as FragmentActivity означает
        // активити как фрагмент активити который наследует родительскийт класс FragmentStateAdapter
        // вторая вещь надо передать список, у нас это frList который состоит из двух фрагментов
        vp.adapter = adapter //подключаем адаптер к вьюпейджер,который передает размер и номер фрагмента
        TabLayoutMediator(tabLayout, vp){
            tab, pos -> tab.text=tList[pos]
        }.attach()
        isSeanck.setOnClickListener{
           checkLocation() //при нажатии на кнопку запускаем функцию для проверки включения gps и запуска интерфейса
        }
        ibSearch.setOnClickListener{
            dialogManager.searchCityDialog(requireContext(), object :dialogManager.Listener{
                override fun onClick(name: String?) {
                    if (name != null) {
                        cityAndCoordinat(name)
                    }
                }
            })
        }
    }
    private fun checkLocation(){ //создаем функцию для проверки включения gps и запуска интерфейса
        if (isLocationEnable()){ //если gps включен то берем координаты
            getLocation()
        } else { // если gps не включен то запускаем диалог
            dialogManager.locationSettingDialog(requireContext(), object :dialogManager.Listener {
                override fun onClick(name:String?) { //запускаем переданный интерфейс Listener через фукцию fun onClick
                // в случае нажатия на кнопку с положительным решением
                   startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) // заускаем Activity другого приложения
                // отвечающего занастройки телефона c помощью Intent ищем Settings(android provayder)
                // ACTION_LOCATION_SOURCE_SETTINGS это вкладка в настройках по gps
                }
            })
        }
    }

    private fun isLocationEnable():Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //проверка включен ли gps для этого нам нужен класс LocationManager в нем есть функции для проверки
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation(){ // функция для получения месторасположения
        val ct = CancellationTokenSource() // создали переменную чтобы из нее вытащить token
        // поскольку token  нужно передать в getCurrentLocation
        if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,ct.token)//getCurrentLocation это получить координаты
            // PRIORITY_HIGH_ACCURACY этос высокой точностью и пофиг на энергопотребление
            .addOnCompleteListener {
                cityAndCoordinat("${it.result.latitude},${it.result.longitude}")
                //передаем координаты чтобы направить запрос на сайт и получить погоду исходя из координат
                //cityAndCoordinat это функция она создана нами ранее
                //.addOnCompleteListener это слушатель куда мыполучаем данные
            }
    }

    private fun updateCurrentCard() = with(binding){ //функция которая будет обновлять данные
        model.liveDataCurrent.observe(viewLifecycleOwner){
            //liveDataCurrent смотри он есть в MainViewModel
            val maxMinTemp = "${it.maxTemp}°C/${it.minTemp}°C"
            //создали отдельную переменную поскольку  унас нет переменной включающей сразу max и min температуру
            tvData.text = it.time
            tvCity.text = it.city
            tvCondition.text = it.condition
            tvCurrentTemp.text = it.currentTemp.ifEmpty { "${it.maxTemp}°C / ${it.minTemp}°C" }
            tvMaxMin.text = if(it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load("https:" + it.imageUrl).into(imWheather) //так пишется для обновления ссылкина картинку
        }
    }

    private fun permissionListener(){ //создали функцию где иницилизируем pLauncher,
// тоесть это в реальном времени  получение разрешения от пользователя
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        //создаем функцию где делаем проверку на то нет ли уже разрешения от пользователя
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            //перевод строки выше - если нет разрешения использования локации пользователя то
            permissionListener() //запускаем созданную функцию,которая запросит разрешение
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)// сам запрос разрешения

        }
    }

    private fun cityAndCoordinat(city:String){ //создаем функцию куда будем передавать название города
        val url = "http://api.weatherapi.com/v1/forecast.json?key= " +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context) // newRequestQueue это и есть наш запрос,
    // функция берется из библиотеки Volley
        val request = StringRequest( //это переменная с запросом,
            // сюда надо передать запрос.метод. внашем случае это GET (взять), ссылку (url)
            Request.Method.GET,
            url,
            {
                result-> funPars(result)//куда должны передоваться данные с сайта
            //здесь будем получать всю информацию которая в Response Body на сайте
            },
            {
                error-> Log.d("MyLog", "Error: $error")
            //тут слушатель на случай возникновения ошибок
            }
        )
        queue.add(request)
    }

    private fun funPars(result: String) { //создали функцию которая будет отвечать за выборку данных из result
        val mainObject = JSONObject(result) //это переменная которая будет содержать наши данные по объектам
        val list = listPars(mainObject) // это переменная которая содержит недостающие данные maxTemp,minTemp,hours
        funObjectPars(mainObject, list[0])
    //запускаем функцию funObjectPars в  ней берем данные из mainObject
    // и из списка list[0] первый элемент поскольку это первый день
    } // funPars это наш первый верхний экран

    private fun listPars (mainObject: JSONObject): List<WeatherModel> {// эту функцию используем для получения данных из списка
        val list = ArrayList<WeatherModel>() //создали пустой список который будем возращать
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        // в daysArray будет записана информация по каждому дню day в виде списков
        val name = mainObject.getJSONObject("location").getString("name")
        //название города нет в массиве поэтому используем запрос к объекту(первый метод)
        for (i in 0 until daysArray.length()) { //замыкаем запрос daysArray для каждого дня
            val day = daysArray[i] as JSONObject //сюда записываем информацию из списка за один день
            val item = WeatherModel(
                name, //город везде одинаковый поэтому его не надо через цикл прогонять
                day.getString("date"),//в day мы ужезашили forecastday поэтому отталкиваемся от него
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
                //это мы сохранили погоду по часам в объект String,
                // чтобы потом в другом фрагменте получить информацию

            )
            list.add(item) //добавляем в список list элементы item
        }
        model.liveDataList.value = list
        return list //опубликовать список
    }

    private fun funObjectPars(mainObject:JSONObject, weatherItem:WeatherModel){// эту функцию используем для заполнения основной карточки приложения
        val item = WeatherModel( //в этой переменной записываем нужные нам значения в WeatherModel
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp, //поскольку мы уже через список передали значения maxTemp,minTemp в WeatherModel
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours
            //поскольку мы уже через список передали значения hours в WeatherModel с помощью fun listPars

        )
        model.liveDataCurrent.value = item //это означает что мы передаем
    // в liveDataCurrent данные из item а updateCurrentCard уже ждет эти данные чтобы обновить их
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
                    }
}