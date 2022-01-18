package com.example.sensor_sample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sensor_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var sensorManager: SensorManager
  private lateinit var significantMotion: Sensor
  private lateinit var stepCounter: Sensor
  private lateinit var stepDetector: Sensor

  private lateinit var binding: ActivityMainBinding

    private val triggerEventListener = object : TriggerEventListener() {
    override fun onTrigger(event: TriggerEvent?) {
      Log.d("SIGNIFICANT_TAG", "sensor: ${event?.sensor}, values: ${event?.values?.firstOrNull()}")
      binding.significantText.text = event?.values.toString()
    }
  }
  private val stepCounterListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
      Log.d("STEP_COUNTER_TAG", "sensor: ${event?.sensor}, values: ${event?.values?.firstOrNull()}")
      binding.stepCountText.text = event?.values.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
  }

  private val stepDetectorListener = object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
      Log.d("STEP_DETECTOR_TAG", "sensor: ${event?.sensor}, values: ${event?.values?.firstOrNull()}")
      binding.stepDetectText.text = event?.values.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    significantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
    stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    setContentView(binding.root)

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      when {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            == PackageManager.PERMISSION_GRANTED -> Unit
        shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
          // TODO: show UI
        }
        else -> {
          val requestPermissionLauncher =  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // TODO:　ｹﾝｹﾞﾝｸﾀﾞｻｲ
          }
          requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    significantMotion.also { sensor ->
      sensorManager.requestTriggerSensor(triggerEventListener, sensor)
    }
    stepCounter.also { sensor ->
      sensorManager.registerListener(stepCounterListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    stepDetector.also { sensor ->
      sensorManager.registerListener(stepDetectorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  override fun onPause() {
    super.onPause()
    significantMotion.also { sensor ->
      sensorManager.cancelTriggerSensor(triggerEventListener, sensor)
    }
    stepCounter.also { sensor ->
      sensorManager.unregisterListener(stepCounterListener, sensor)
    }
    stepDetector.also { sensor ->
      sensorManager.unregisterListener(stepDetectorListener, sensor)
    }
  }
}