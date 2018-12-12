package abc.kintegratedtest

import android.app.Activity
import android.os.Bundle
import abc.kintegratedtest.R.layout.*
import android.graphics.Color
import android.graphics.Point
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.activity_chart.*

@Suppress("UNCHECKED_CAST")
class ChartActivity : Activity(){

    private val feelList by lazy { intent.getSerializableExtra("feelList") as ArrayList<Float> }
    private val weatherList by lazy { intent.getSerializableExtra("weatherList") as ArrayList<Float> }
    private val feelStr = arrayOf("최고예요", "좋아요", "그저 그래요", "슬퍼요", "화나요")
    private val weatherStr = arrayOf("맑아요", "구름꼈어요", "바람불어요", "비와요", "눈와요")

    private val pieChartFeel by lazy { chart_feel }
    private val pieChartWeather by lazy { chart_weather }
    private val tvDateChart by lazy { tv_date_chart }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_chart)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size) // 화면 크기 얻기

        pieChartFeel.layoutParams.width = size.x
        pieChartFeel.layoutParams.height = pieChartFeel.layoutParams.width * 7 / 10
        pieChartWeather.layoutParams.width = pieChartFeel.layoutParams.width
        pieChartWeather.layoutParams.height = pieChartFeel.layoutParams.width * 7 / 10

        tvDateChart.text = intent.getStringExtra("dateText")

        // Feel, Weather 공통 변수
        val description = Description()

        /////////////////
        // Feel 그래프 //
        /////////////////
        description.text = ""
        pieChartFeel.description = description
        pieChartFeel.isRotationEnabled = true // 그래프 회전 여부
        pieChartFeel.setUsePercentValues(true)
        //pieChartFeel.setHoleColor(Color.rgb(123, 123, 123)) // 원 내부 배경색
        //pieChartFeel.setCenterTextColor(Color.BLACK) // 원 내부 텍스트 색상
        pieChartFeel.holeRadius = 50f // 원 내부 크기
        pieChartFeel.setTransparentCircleAlpha(150) // 그래프와 원 내부 간 경계선 투명도
        pieChartFeel.centerText = "기분"
        pieChartFeel.setCenterTextSize(20f) // 원 내부 텍스트 크기

        val xEntriesFeel = ArrayList<String>()
        val yEntriesFeel = ArrayList<PieEntry>()

        xEntriesFeel.addAll(feelStr)
        for (i in 0 until feelList.size) yEntriesFeel.add(PieEntry(feelList[i], i))

        // DataSet 생성
        val pieDataSetFeel = PieDataSet(yEntriesFeel, "기분")
        pieDataSetFeel.sliceSpace = 2f // 그래프 내 각 데이터 간 여백
        //pieDataSetFeel.selectionShift =12.0f // 선택된 데이터 그래프 크기(기본 12.0f)
        pieDataSetFeel.valueTextSize = 12f // 그래프 내 데이터 텍스트 크기

        // DataSet 색상 설정
        val colorsFeel = ArrayList<Int>()
        colorsFeel.add(Color.rgb(190, 186, 218))
        colorsFeel.add(Color.rgb(251, 128, 114))
        colorsFeel.add(Color.rgb(128, 177, 211))
        colorsFeel.add(Color.rgb(253, 180, 98))
        colorsFeel.add(Color.rgb(179, 222, 105))

        pieDataSetFeel.colors = colorsFeel

        // 범례 추가
        val legendFeel = pieChartFeel.legend

        legendFeel.form = Legend.LegendForm.CIRCLE
        legendFeel.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legendFeel.textSize = 12f

        // 범례에 각 데이터 내역 삽입
        val entriesFeel : MutableList<LegendEntry> = ArrayList()

        for (i in 0 until xEntriesFeel.size) {
            val entry = LegendEntry()
            entry.formColor = colorsFeel[i]
            entry.label = xEntriesFeel[i]
            entriesFeel.add(entry)
        }

        legendFeel.setCustom(entriesFeel)

        // Pie 데이터 객체 생성
        val pieDataFeel = PieData(pieDataSetFeel)
        pieChartFeel.data = pieDataFeel
        pieChartFeel.invalidate()

        pieChartFeel.animateY(1500) // 그래프 펼쳐지는 시간

        pieChartFeel.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() { pieChartFeel.centerText = "기분" }

            override fun onValueSelected(entry: Entry?, highlight: Highlight) {
                pieChartFeel.centerText = "${feelStr[highlight.x.toInt()]}\n${highlight.y.toInt()} 회"
            }
        })

        ////////////////////
        // Weather 그래프 //
        ////////////////////
        description.text = ""
        pieChartWeather.description = description
        pieChartWeather.isRotationEnabled = true // 그래프 회전 여부
        pieChartWeather.setUsePercentValues(true)
        //pieChartWeather.setHoleColor(Color.rgb(123, 123, 123)) // 원 내부 배경색
        //pieChartWeather.setCenterTextColor(Color.BLACK) // 원 내부 텍스트 색상
        pieChartWeather.holeRadius = 50f // 원 내부 크기
        pieChartWeather.setTransparentCircleAlpha(150) // 그래프와 원 내부 간 경계선 투명도
        pieChartWeather.centerText = "날씨"
        pieChartWeather.setCenterTextSize(20f) // 원 내부 텍스트 크기

        val xEntriesWeather = ArrayList<String>()
        val yEntriesWeather = ArrayList<PieEntry>()

        xEntriesWeather.addAll(weatherStr)
        for (i in 0 until weatherList.size) yEntriesWeather.add(PieEntry(weatherList[i], i))

        // DataSet 생성
        val pieDataSetWeather = PieDataSet(yEntriesWeather, "기분")
        pieDataSetWeather.sliceSpace = 2f // 그래프 내 각 데이터 간 여백
        //pieDataSetWeather.selectionShift =12.0f // 선택된 데이터 그래프 크기(기본 12.0f)
        pieDataSetWeather.valueTextSize = 12f // 그래프 내 데이터 텍스트 크기

        // DataSet 색상 설정
        val colorsWeather = ArrayList<Int>()
        colorsWeather.add(Color.rgb(190, 186, 218))
        colorsWeather.add(Color.rgb(251, 128, 114))
        colorsWeather.add(Color.rgb(128, 177, 211))
        colorsWeather.add(Color.rgb(253, 180, 98))
        colorsWeather.add(Color.rgb(179, 222, 105))

        pieDataSetWeather.colors = colorsWeather

        // 범례 추가
        val legendWeather = pieChartWeather.legend

        legendWeather.form = Legend.LegendForm.CIRCLE
        legendWeather.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legendWeather.textSize = 12f

        // 범례에 각 데이터 내역 삽입
        val entriesWeather : MutableList<LegendEntry> = ArrayList()

        for (i in 0 until xEntriesWeather.size) {
            val entry = LegendEntry()
            entry.formColor = colorsWeather[i]
            entry.label = xEntriesWeather[i]
            entriesWeather.add(entry)
        }

        legendWeather.setCustom(entriesWeather)

        // Pie 데이터 객체 생성
        val pieDataWeather = PieData(pieDataSetWeather)
        pieChartWeather.data = pieDataWeather
        pieChartWeather.invalidate()

        pieChartWeather.animateY(1500) // 그래프 펼쳐지는 시간

        pieChartWeather.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() { pieChartWeather.centerText = "기분" }

            override fun onValueSelected(entry: Entry?, highlight: Highlight) {
                pieChartWeather.centerText = "${weatherStr[highlight.x.toInt()]}\n${highlight.y.toInt()} 회"
            }
        })
    }
}