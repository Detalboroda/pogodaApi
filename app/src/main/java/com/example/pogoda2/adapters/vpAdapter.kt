package com.example.pogoda2.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class vpAdapter(fa: FragmentActivity,private val list:List<Fragment>):FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return list.size
//сюда нужно передать колличество элементов которое у нас будет в адапторе,тобишь его размер
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
// тут по позиции берется фрагмент, тоесть каждый раз когда мы перелистываем фрагмент,
// по номеру позиции заменяет на новый фрагмент, тоесть сам список

    }
}