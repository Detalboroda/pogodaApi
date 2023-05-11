package com.example.pogoda2

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object dialogManager {
    fun locationSettingDialog (context: Context, listener:Listener) { //код для создания диалога
        val builder = AlertDialog.Builder(context)
        val dialog =builder.create()
        dialog.setTitle("Enable location?") //выводим сообщения
        dialog.setMessage("Location disable,do you want enable location?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){ //кнопка для положительного решения
            _,_ ->
            listener.onClick(null) // для нажатия положительной кнопки запускается интерфейс через фукцию onClick
            dialog.dismiss() //_,_ этодве переменные которые нам не нужны,
        // чтобы их не использовать указывем нижнее подчеркивание,dismiss() закрывает диалог
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ //кнопка для отрицательного решения
                _,_ -> dialog.dismiss()
        }
        dialog.show() //показываем диалог
    }
    fun searchCityDialog (context: Context, listener:Listener) { //код для создания диалога поиска города
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context) // создали переменнуюдля текста
        builder.setView(edName)
        val dialog =builder.create()
        dialog.setTitle("City name:") //выводим сообщение
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok"){ //кнопка для положительного решения
                _,_ ->
            listener.onClick(edName.text.toString()) // для нажатия положительной кнопки запускается интерфейс через фукцию onClick
            dialog.dismiss() //_,_ этодве переменные которые нам не нужны,
            // чтобы их не использовать указывем нижнее подчеркивание,dismiss() закрывает диалог
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){ //кнопка для отрицательного решения
                _,_ -> dialog.dismiss()
        }
        dialog.show() //показываем диалог
    }
    interface Listener {
        fun onClick(name:String?) // name:String? добавили для fun searchCityDialog,
    // это если принажатии кнопки передается текстгорода,
    //и когда текст города не передается то может быть null это и означает String?
    } //создаем мост между диалогом и мэин фрагментом,
// чтобы вслучае нажатия положительной кнопки открывать настройки на телефоне для GPS
}