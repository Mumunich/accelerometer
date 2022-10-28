package com.mumunich.accelerometer

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var sManager:SensorManager
    private var magnetic = FloatArray(9)
    private var gravity = FloatArray(9)

    private var acceleration = FloatArray(3)
    private var magneticField = FloatArray(3)
    private var values = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textViewSensor:TextView = findViewById(R.id.tvSensor)
        val layoutRotation:LinearLayout = findViewById(R.id.layoutRotation)
        // Получаем сенсор
        sManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensor2 = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val sListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                 when(event?.sensor?.type){
                     // заполняем массивы данных с сенсора
                     Sensor.TYPE_ACCELEROMETER -> acceleration = event.values.clone()
                     Sensor.TYPE_MAGNETIC_FIELD -> magneticField = event.values.clone()
                 }
                // Передаём 2 пустых массива,функция их заполнит и 2 массива с данными сенсоров
                SensorManager.getRotationMatrix(gravity,magnetic,acceleration,magneticField)
                val outGravity = FloatArray(9)
                // но эта функция выдаёт данные в другой системе координат,поэтому нужен ремап в новый массив
                SensorManager.remapCoordinateSystem(gravity,SensorManager.AXIS_X,SensorManager.AXIS_Z,outGravity)
                SensorManager.getOrientation(outGravity,values)

                // Крутим палочку
                val degree = values[2] * 57.2958f
                // Устанавливаем палку,для поворота экрана
                val rotate = 270 + degree
                layoutRotation.rotation = rotate
                val rData = 90 + degree
                val color = if(rData.toInt() == 0){
                    Color.GREEN
                } else{
                    Color.RED
                }
                layoutRotation.setBackgroundColor(color)
                textViewSensor.text = rData.toInt().toString()

            }
            // При изменении точности измерений
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
        // Добавляем слушатель
        sManager.registerListener(sListener,sensor,SensorManager.SENSOR_DELAY_NORMAL)
        sManager.registerListener(sListener,sensor2,SensorManager.SENSOR_DELAY_NORMAL)
    }
}