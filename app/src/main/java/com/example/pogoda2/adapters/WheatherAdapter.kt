package com.example.pogoda2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pogoda2.R
import com.example.pogoda2.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WheatherAdapter(val listener:Listener?):ListAdapter<WeatherModel,WheatherAdapter.Holder> (Comparator()){
    class Holder(view:View, val listener:Listener?):RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        // это чтобы не искать с помощью findViewbyid каждый элемент view
        //ListItemBinding это список который будет хранить данные кадого элемента нашей разметки
        //тоесть мы берем шаблон нашего view и передаем его в ListItemBinding
        // и теперь будет доступ ко всем его элементам
        var itemTemp:WeatherModel? = null
        init {
            itemView.setOnClickListener{
                itemTemp?.let { it1 -> listener?.onClick(it1) }//чтобы передать сюда item
            // делаем локальную переменную var itemTemp:WeatherModel? = null
            // и заполняем эту переменную в fun bind(item:WeatherModel) где есть item,тоесть  itemTemp = item
            }//при нажатии на картинку погоды дня itemView
        }

        fun bind(item:WeatherModel) = with(binding){
            itemTemp = item
            tvDate.text = item.time
            tvCondishion.text = item.condition
            tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp}°C / ${item.minTemp}°C" } //ifEmpty тоесть если пустое значение то значение из скобок
            Picasso.get().load("https:" + item.imageUrl).into(im)
        //ссылку берем из item а im это картинка которуюобновляем в list_item
        }

    }
    class Comparator: DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return  oldItem == newItem
        //если в базе данных есть уникальные элементы то их нужно сравнивать в ЭТОЙ функции указывая id
        //например return oldItem.id.city == newItem.id.city
            // тоесть этот элемент должен быть в одной базе данных а в обновленной допустим нет
        }


        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return  oldItem == newItem
            // в этой функции сравнивается весь список
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder { //тут создали Holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
    //LayoutInflater загружает в память из (from) контекста,
    // но у нас контекст есть во ViewGroup это parent поэтому выдергивает от туда
    //а теперь надуваем inflate макет layout в нашем макете list_item.xml нашим контекстом parent
        return Holder(view, listener) //вернуть наш холдер
    // (тоесть сохранили в память, там и все элементы class holder тожесохранились) со вью(разметкой),
    // чтобы все повторить нужное колличество раз
    }

    override fun onBindViewHolder(holder: Holder, position: Int) { //Тут заполнили
        holder.bind(getItem(position))
    //для нашего holder с помощью bind берем элементы с помощью getItem  по позиции 0..1..2.. и т.д.
    }

    interface Listener{
        fun onClick(item:WeatherModel) //достаем информацию из WeatherModel
    // где есть необходимые данные а именно val hours:String
    }
}