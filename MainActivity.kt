package abc.kintegratedtest

import abc.kintegratedtest.R.drawable.*
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import abc.kintegratedtest.R.layout.*
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_date.view.*
import java.util.*

class MainActivity : AppCompatActivity() {
    // DB, RecyclerView 관련
    private lateinit var db : DBAdapter
    private lateinit var reView : RecyclerView
    private lateinit var recyclerCursorAdapter: RecyclerCursorAdapter
    private val queryDefaultFront = "select * from memo "
    private val queryDefaultEnd = "order by _id desc"
    private var query : String
    private lateinit var c : Cursor

    // Date 관련
    private lateinit var tvDate : TextView
    private var fY : Int /* DB 기간 검색용 */
    private var fM : Int
    private var tY : Int
    private var tM : Int
    private val dateTextDefaultFront = "기간 : "
    private val dateTextDefaultAll = "전체"
    private var dateText : String
    private var isCancel: Boolean

    // Delete 관련
    private var isDelete = false
    private lateinit var btnLeft : Button
    private lateinit var btnCenter : Button
    private lateinit var btnRight : Button

    init { // 별 문제 없으면 여기서 초기화
        // Date
        val calendar : Calendar = Calendar.getInstance()
        fY = calendar.get(Calendar.YEAR)
        fM = calendar.get(Calendar.MONTH) + 1
        tY = calendar.get(Calendar.YEAR)
        tM = calendar.get(Calendar.MONTH) + 1

        // 변수
        dateText = dateTextDefaultFront + dateTextDefaultAll

        isCancel = false

        query = queryDefaultFront + queryDefaultEnd
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDate = tv_btn_date
        tvDate.text = dateText
        reView = review

        // RecyclerView 에 Cursor 내역 삽입 (DB -> Cursor -> Array -> Adapter -> RecyclerView)
        db = DBAdapter(this, DBAdapter.READABLE)
        c = db.rawQuery(query)
        recyclerCursorAdapter = RecyclerCursorAdapter(this, c, query)

        reView.adapter = recyclerCursorAdapter

        btnLeft = btn_chart
        btnCenter = btn_delete
        btnRight = btn_create
    }

    override fun onResume() {
        super.onResume()
        println("onResume()")

        c = db.rawQuery(query)
        recyclerCursorAdapter.changeCursor(c, query)
    }

    fun onClickDate(v : View) { dialogSetDate(false) }

    fun onClickChart(v : View) {
        if (!isDelete) dialogSetDate(true)
        else {
            if (!recyclerCursorAdapter.isAllChecked()) {
                recyclerCursorAdapter.setItemChecked(true)
                btnLeft.setBackgroundResource(cb_checked)
                btnCenter.setBackgroundResource(ic_trashcan_red)
            }
            else {
                recyclerCursorAdapter.setItemChecked(false)
                btnLeft.setBackgroundResource(cb_unchecked)
                btnCenter.setBackgroundResource(ic_trashcan_grey)
            }
        }
    }

    fun onClickDelete(v : View) {
        if (isDelete) {
            if (recyclerCursorAdapter.isNothingChecked()) { Toast.makeText(this, "하나 이상 체크해주세요.", Toast.LENGTH_SHORT).show() }
            else {
                db = DBAdapter(this, DBAdapter.WRITABLE)

                for (i in 0 until recyclerCursorAdapter.itemCount) {
                    if (recyclerCursorAdapter.checkList[i]) {
                        c.moveToPosition(i)
                        db.delete("memo", "_id=?", arrayOf(c.getString(0)))
                    }
                }
                Toast.makeText(this, "삭제되었어요.", Toast.LENGTH_SHORT).show()

                c = db.rawQuery(query)
                recyclerCursorAdapter.changeCursor(c, query)
                btnCenter.setBackgroundResource(ic_trashcan_grey)
            }
        } else {
            isDelete = true
            btnLeft.setBackgroundResource(cb_unchecked)
            btnCenter.setBackgroundResource(ic_trashcan_grey)
            btnRight.setBackgroundResource(ic_cancel)
        }
        recyclerCursorAdapter.toggleVisibility(isDelete)
    }

    fun onClickCreateMemo(v : View) {
        if (!isDelete) { // 메모 추가 버튼
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("isCreate", true)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        else { // 삭제 취소 버튼
            isDelete = false
            btnLeft.setBackgroundResource(ic_chart)
            btnCenter.setBackgroundResource(ic_trashcan)
            btnRight.setBackgroundResource(ic_pencil)

            recyclerCursorAdapter.setItemChecked(false) // 전체 체크 해제

            recyclerCursorAdapter.toggleVisibility(isDelete)
        }
    }

    private fun dialogSetDate(isChart : Boolean) {
        val inflater = layoutInflater

        val dialogView = inflater.inflate(dialog_date, null as ViewGroup?)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("기간 설정")
        builder.setView(dialogView)
        builder.setCancelable(false)

        val calendar = Calendar.getInstance()
        val npFromYear = dialogView.np_fromYear
        npFromYear.maxValue = calendar.get(Calendar.YEAR)
        npFromYear.minValue = npFromYear.maxValue - 10
        npFromYear.value = npFromYear.maxValue
        val npFromMonth = dialogView.np_fromMonth
        npFromMonth.maxValue = 12
        npFromMonth.minValue = 1
        npFromMonth.value = calendar.get(Calendar.MONTH) + 1
        val npToYear = dialogView.np_toYear
        npToYear.maxValue = npFromYear.maxValue
        npToYear.minValue = npFromYear.minValue
        npToYear.value = npFromYear.value
        val npToMonth = dialogView.np_toMonth
        npToMonth.maxValue = npFromMonth.maxValue
        npToMonth.minValue = npFromMonth.minValue
        npToMonth.value = npFromMonth.value

        builder.setPositiveButton("적용") {_, _ -> }

        builder.setNeutralButton("모두 보기") {dialogInterface, _ ->
            fY = 0
            fM = 0
            tY = 0
            tM = 0

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("취소") {dialogInterface, _ ->
            isCancel = true
            dialogInterface.cancel()
        }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            fY = npFromYear.value
            fM = npFromMonth.value
            tY = npToYear.value
            tM = npToMonth.value
            val isClose = if (fY < tY || (fY == tY && fM <= tM)) true else { Toast.makeText(applicationContext, "시작 날짜가 더 커요.", Toast.LENGTH_SHORT).show(); false }

            if (isClose) dialog.dismiss()
        }

        dialog.setOnDismissListener {
            // 조건문 내용 정하기
            var conditions = ""
            if (fY != 0) conditions = "where (date_year between $fY and $tY) and not(date_year = $fY and date_month < $fM) and not(date_year = $tY and date_month > $tM)"

            query = queryDefaultFront + conditions + queryDefaultEnd
            c = db.rawQuery(query)

            // TextView 에 들어갈 내용 정하기
            dateText = if (fY != 0) {
                if (fY == tY) {
                    if (fM == tM) "$dateTextDefaultFront$fY.$fM" else "$dateTextDefaultFront$fY.$fM - $tM"
                } else "$dateTextDefaultFront$fY.$fM - $tY.$tM"
            } else dateTextDefaultFront + dateTextDefaultAll

            if (!isChart) { // 메모 리스트에 대한 기간 설정 시
                tvDate.text = dateText

                recyclerCursorAdapter.changeCursor(c, query)
            }
            else { // 차트 버튼으로 접근 시
                if (isCancel) { // 취소 여부 확인
                    isCancel = false
                    return@setOnDismissListener
                }

                if (c.count != 0) {
                    var chartQuery = "select feel_id, weather_id from memo $conditions"

                    chartQuery += if (fY == 0) "where " else "and "

                    val feelList = ArrayList<Float>()

                    for (i in 0..4) {
                        c = db.rawQuery("${chartQuery}feel_id = ${String.format("$i")}")
                        feelList.add(c.count.toFloat())
                    }

                    val weatherList = ArrayList<Float>()

                    for (i in 0..4) {
                        c = db.rawQuery("${chartQuery}weather_id = ${String.format("$i")}")
                        weatherList.add(c.count.toFloat())
                    }

                    val intent = Intent(applicationContext, ChartActivity::class.java)

                    intent.putExtra("feelList", feelList)
                    intent.putExtra("weatherList", weatherList)
                    intent.putExtra("dateText", dateText)

                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(intent)
                }
                else Toast.makeText(applicationContext, "검색된 내용이 없어요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
