package abc.kintegratedtest

import android.app.Activity
import abc.kintegratedtest.R.layout.activity_edit
import abc.kintegratedtest.R.drawable.*
import abc.kintegratedtest.R.layout.spinner_item
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_edit.*
import java.util.*

class EditActivity : Activity() {
    private val etText by lazy { et_text }
    private val spinFeel by lazy { spin_feel }
    private val spinWeather by lazy { spin_weather }
    private val btnModify by lazy { btn_modify }

    private val isCreate by lazy { intent.getBooleanExtra("isCreate", true) }
    private val updateId by lazy { intent.getStringExtra("_id") }

    var feelIndex = -1
    var weatherIndex = -1
    private lateinit var adapterFeel : CustomArrayAdapter
    private lateinit var adapterWeather : CustomArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_edit)

        // Layout 초기화
        Runnable {
            val itemsFeel : MutableList<SpinnerItem> = ArrayList()
            itemsFeel.add(SpinnerItem(excited, "최고예요"))
            itemsFeel.add(SpinnerItem(happy, "좋아요"))
            itemsFeel.add(SpinnerItem(worried, "그저 그래요"))
            itemsFeel.add(SpinnerItem(sad, "슬퍼요"))
            itemsFeel.add(SpinnerItem(angry, "화나요"))

            adapterFeel = CustomArrayAdapter(this, spinner_item, itemsFeel)
            spinFeel.adapter = adapterFeel

            val itemsWeather : MutableList<SpinnerItem> = ArrayList()
            itemsWeather.add(SpinnerItem(sunny, "맑아요"))
            itemsWeather.add(SpinnerItem(cloud, "구름투성이"))
            itemsWeather.add(SpinnerItem(windy, "바람불어요"))
            itemsWeather.add(SpinnerItem(rainy, "비와요"))
            itemsWeather.add(SpinnerItem(snowy, "눈와요"))

            adapterWeather = CustomArrayAdapter(this, spinner_item, itemsWeather)
            spinWeather.adapter = adapterWeather
        }.run()

        if (isCreate) btnModify.text = "추가"
        else {
            btnModify.text = "수정"

            etText.setText(intent.getStringExtra("text"))
            spinFeel.setSelection(intent.getIntExtra("feel_index", 0))
            spinWeather.setSelection(intent.getIntExtra("weather_index", 0))
        }

        spinFeel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) { feelIndex = i }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
        spinWeather.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) { weatherIndex = i }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }

    fun onClickModify(v : View) {
        if (etText.text.toString() != "") { // 텍스트 여부 체크
            val calendar = Calendar.getInstance()

            val db = DBAdapter(this, DBAdapter.WRITABLE)

            val values = ContentValues()

            values.put("content", etText.text.toString())
            values.put("feel_id", feelIndex)
            values.put("weather_id", weatherIndex)

            if (isCreate) {
                values.put("date_year", calendar.get(Calendar.YEAR))
                values.put("date_month", calendar.get(Calendar.MONTH) + 1)
                values.put("date_day", calendar.get(Calendar.DATE))

                db.insert("memo", null, values)
            }
            else {
                db.update("memo", values, "_id=?", arrayOf(updateId))
                Toast.makeText(this, "수정되었어요.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        else {
            Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}