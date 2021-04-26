package de.vogella.android.besserwisser

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var speedSwitch = false
    private var lightSwitch = false
    private var temperatureSwitch = false
    private var id = ""
    var speedAverage: ArrayList<Float> = ArrayList()
    var lightAverage: ArrayList<Float> = ArrayList()
    private val apiCalls = ApiCalls()
    var temperatureAverage: ArrayList<Float> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var x = this.findViewById<View>(R.id.idText) as EditText
        val btn_click_me = findViewById(R.id.goButtons) as Button
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
            }
        }
// 3
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.also { magneticField ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
            }
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.also { magneticField ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
            }
        }

        // set on-click listener
        btn_click_me.setOnClickListener {
            // your code to perform when the user clicks on the button
            setContentView(R.layout.new_main)
            id=x.text.toString() as String

            Toast.makeText(this@MainActivity, x.text, Toast.LENGTH_SHORT).show()
            val switch1 = findViewById<View>(R.id.switch1) as Switch
            val switch2 = findViewById<View>(R.id.switch2) as Switch
            val switch3 = findViewById<View>(R.id.switch3) as Switch
            switch1.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                if (b) {
                    speedSwitch = true;
                    Toast.makeText(this@MainActivity, "speed sensor is run", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "speed sensor is not run ", Toast.LENGTH_SHORT).show()
                    speedSwitch = false;
                }
            }
            switch2.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                if (b) {
                    temperatureSwitch = true;
                    Toast.makeText(this@MainActivity, "temperature sensor is run", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "temperature sensor is not run", Toast.LENGTH_SHORT).show()
                    temperatureSwitch = false;
                }
            }
            switch3.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                if (b) {

                    lightSwitch = true;

                    Toast.makeText(this@MainActivity, "light sensor  is run", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "light sensor is not run ", Toast.LENGTH_SHORT).show()
                    lightSwitch = false;
                }
            }
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val current = LocalDateTime.now()
                if (speedSwitch) {
                    if (!speedAverage.isEmpty()) {
                        val averageValues = speedAverage.average()
                        apiCalls.postSensorValuesAverageToServer(id,"speed",averageValues)
                        speedAverage.clear()
                    }
                }
                if (temperatureSwitch) {
                    if (!temperatureAverage.isEmpty()) {
                        val averageValues = temperatureAverage.average()
                        apiCalls.postSensorValuesAverageToServer(id,"temperature",averageValues)
                        temperatureAverage.clear()
                    }
                }
                if (lightSwitch) {
                    if (!lightAverage.isEmpty()) {
                        val averageValues = lightAverage.average()
                        apiCalls.postSensorValuesAverageToServer(id,"light",averageValues)
                        lightAverage.clear()
                    }

                }
            }
        }, 1000, 5000) // Minimum delay is 1 millisecond. If lower, it throws IllegalArgumentException

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) {
            return
        }
        if (event != null) {
            if (speedSwitch) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    speedAverage.add(event.values[0])
                }
            }
            if (lightSwitch) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    Toast.makeText(this@MainActivity, event.values[0].toString(), Toast.LENGTH_SHORT).show()
                    lightAverage.add(event.values[0])

                }
            }
            if (temperatureSwitch) {
                if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    temperatureAverage.add(event.values[0])
                }

            }
        }
    }
//    protected fun sendJson(play: String?, prop: String?) {
//        val t: Thread = object : Thread() {
//            override fun run() {
//                Looper.prepare() //For Preparing Message Pool for the childThread
//                val client: HttpClient = DefaultHttpClient()
//                HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000) //Timeout Limit
//                val response: HttpResponse
//                val json = JSONObject()
//                try {
//                    val post = HttpPost("http://192.168.178.65:8080/contacts")
//                    json.put("Firstname", play)
//                    json.put("Lastname", prop)
//                    val se = StringEntity(json.toString())
//                    se.setContentType(BasicHeader(HTTP.CONTENT_TYPE, "application/json"))
//                    post.setEntity(se)
//                    response = client.execute(post)
//
//                    /*Checking response */if (response != null) {
//                        val `in`: InputStream = response.getEntity().getContent() //Get the data in the entity
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                Looper.loop() //Loop in the message queue
//            }
//        }
//        t.start()
//    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}

