package com.example.pogoda2.fragments

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionGranted(p:String):Boolean {
//функция которая проверяет значение полученное от пользователя
// p:String "p" это унас будет переменная для названий разрешений
// Boolean используемпотому что будет только два значения true ибо false
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) ==
            PackageManager.PERMISSION_GRANTED
    //checkSelfPermission возвращает два числа -1(false) и 0(true)
// но у нас Boolean поэтому нужно сделать сравнение, PERMISSION_GRANTED содержит константу число 0
// поэтому checkSelfPermission возвращает -1 то -1 не равно 0 и будет false, а если 0==0 то true
}