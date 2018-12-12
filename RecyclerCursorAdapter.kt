package abc.kintegratedtest

import abc.kintegratedtest.R.drawable.*
import abc.kintegratedtest.R.id.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class RecyclerCursorAdapter(private val context: Context, private var cursor : Cursor, private var query : String) : CursorRecyclerViewAdapter<RecyclerCursorAdapter.ViewHolder>(cursor) {

    private var bClick = false
    private var check = false
    private lateinit var hsv : FloatArray
    private var chkCount = 0

    val checkList = ArrayList<Boolean>()

    init { for (i in 0 until cursor.count) checkList.add(false) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor?) {
        viewHolder.tvText.text = cursor!!.getString(1)

        when (cursor.getInt(2)) {
            0 -> viewHolder.ivFeel.setImageResource(excited)
            1 -> viewHolder.ivFeel.setImageResource(happy)
            2 -> viewHolder.ivFeel.setImageResource(worried)
            3 -> viewHolder.ivFeel.setImageResource(sad)
            4 -> viewHolder.ivFeel.setImageResource(angry)
        }
        when (cursor.getInt(3)) {
            0 -> viewHolder.ivWeather.setImageResource(sunny)
            1 -> viewHolder.ivWeather.setImageResource(cloud)
            2 -> viewHolder.ivWeather.setImageResource(windy)
            3 -> viewHolder.ivWeather.setImageResource(rainy)
            4 -> viewHolder.ivWeather.setImageResource(snowy)
        }
        viewHolder.tvCreatedDate.text = String.format("${cursor.getInt(4)}. ${cursor.getInt(5)}. ${cursor.getInt(6)}")

        hsv = floatArrayOf(360f / itemCount * cursor.position + 1, 0.12f, 1.6f)
        viewHolder.itemView.setBackgroundColor(Color.HSVToColor(hsv))

        if (bClick) viewHolder.cb.visibility = View.VISIBLE
        else viewHolder.cb.visibility = View.GONE

        viewHolder.cb.isChecked = check
    }

    fun changeCursor(cursor: Cursor?, query: String) { // change Cursor and Query
        changeCursor(cursor)
        this.query = query
        refreshCheckedList()
    }

    override fun changeCursor(cursor: Cursor?) { // swapCursor 실행 -> 이전 Cursor 닫기 -> 매개 변수의 Cursor 교체(바꾸지 않으면 처음 앱 실행 시 지정된 cursor 로 고정)
        super.changeCursor(cursor)
        if (cursor != null) this.cursor = cursor
    }

    fun toggleVisibility(bClick : Boolean) {
        this.bClick = bClick
        chkCount = 0
        notifyDataSetChanged()
    }

    fun setItemChecked(check : Boolean) {
        this.check = check
        chkCount = 0
        if (!check) refreshCheckedList()
        notifyDataSetChanged()
    }

    fun isAllChecked() = chkCount == itemCount
    fun isNothingChecked() = chkCount == 0

    private fun refreshCheckedList() { checkList.clear(); for (i in 0 until cursor.count) checkList.add(false) }

    @SuppressLint("Recycle")
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvText : TextView = itemView.findViewById(tv_text)
        var ivFeel : ImageView = itemView.findViewById(iv_feel)
        var ivWeather : ImageView = itemView.findViewById(iv_weather)
        var tvCreatedDate : TextView = itemView.findViewById(tv_createdDate)
        var cb : CheckBox = itemView.findViewById(cb_isDeleteItem)

        init {
            itemView.setOnClickListener {
                println("현재 위치:$layoutPosition in layoutPosition")

                if (cb.visibility == View.GONE) { // isDelete 변수 체크 -> 체크박스 visibility 체크로 대체
                    // 새 다이얼로그 생성
                    val inflater = LayoutInflater.from(context)

                    val dialogView = inflater.inflate(R.layout.dialog_itemview, null as ViewGroup?)

                    hsv = floatArrayOf(360f / itemCount * layoutPosition + 1, 0.12f, 1.6f)
                    dialogView.setBackgroundColor(Color.HSVToColor(hsv))

                    val builder = AlertDialog.Builder(context)
                    builder.setView(dialogView)
                    builder.setCancelable(false)

                    // Layout Data Setting
                    val ivFeelItemView : ImageView = dialogView.findViewById(iv_feel_itemView)
                    val tvFeelItemView : TextView = dialogView.findViewById(tv_feel_itemView)
                    val ivWeatherItemView : ImageView = dialogView.findViewById(iv_weather_itemView)
                    val tvWeatherItemView : TextView = dialogView.findViewById(tv_weather_itemView)
                    val tvContentItemView : TextView = dialogView.findViewById(tv_content_itemView)
                    tvContentItemView.movementMethod = ScrollingMovementMethod() // Set to scrollable

                    cursor.moveToPosition(layoutPosition)

                    println("layoutPosition : $layoutPosition, cursor.getInt(2) : ${cursor.getInt(2)}, cursor.getInt(3) : ${cursor.getInt(3)}")
                    when (cursor.getInt(2)) {
                        0 -> { ivFeelItemView.setImageResource(excited); tvFeelItemView.text = "최고예요" }
                        1 -> { ivFeelItemView.setImageResource(happy); tvFeelItemView.text = "좋아요"}
                        2 -> { ivFeelItemView.setImageResource(worried); tvFeelItemView.text = "그저 그래요"}
                        3 -> { ivFeelItemView.setImageResource(sad); tvFeelItemView.text = "슬퍼요"}
                        4 -> { ivFeelItemView.setImageResource(angry); tvFeelItemView.text = "화나요"}
                    }
                    when (cursor.getInt(3)) {
                        0 -> { ivWeatherItemView.setImageResource(sunny); tvWeatherItemView.text = "맑아요" }
                        1 -> { ivWeatherItemView.setImageResource(cloud); tvWeatherItemView.text = "구름꼈어요"}
                        2 -> { ivWeatherItemView.setImageResource(windy); tvWeatherItemView.text = "바람불어요"}
                        3 -> { ivWeatherItemView.setImageResource(rainy); tvWeatherItemView.text = "비와요"}
                        4 -> { ivWeatherItemView.setImageResource(snowy); tvWeatherItemView.text = "눈와요"}
                    }
                    tvContentItemView.text = cursor.getString(1)

                    builder.setNeutralButton("삭제") { _, _ ->
                        println("현재 위치:$layoutPosition in layoutPosition")
                        println("현재 위치:$adapterPosition in adapterPosition")
                        println("현재 위치:$oldPosition in oldPosition")
                        cursor.moveToPosition(layoutPosition)

                        val db = DBAdapter(context, DBAdapter.WRITABLE)
                        db.delete("memo", "_id=?", arrayOf(cursor.getString(0)))

                        cursor = db.rawQuery(query)
                        changeCursor(cursor)
                        Toast.makeText(context.applicationContext, "삭제되었어요.", Toast.LENGTH_SHORT).show()
                    }

                    builder.setNegativeButton("수정") { _, _ ->
                        val intent = Intent(context.applicationContext, EditActivity::class.java)

                        intent.putExtra("isCreate", false)
                        println("현재 위치:$layoutPosition in layoutPosition")
                        println("현재 위치:$adapterPosition in adapterPosition")
                        println("현재 위치:$oldPosition in oldPosition")
                        cursor.moveToPosition(layoutPosition)
                        intent.putExtra("_id", cursor.getString(0))
                        intent.putExtra("text", cursor.getString(1))
                        intent.putExtra("feel_index", cursor.getInt(2))
                        intent.putExtra("weather_index", cursor.getInt(3))

                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                        context.startActivity(intent)
                    }

                    builder.setPositiveButton("확인") { _, _ -> }

                    val dialog : AlertDialog = builder.create()
                    dialog.show()
                }
                else { // 아이템 체크박스 변경 시
                    cb.isChecked = !cb.isChecked
                    checkList[layoutPosition] = !checkList[layoutPosition]

                    if (cb.isChecked) chkCount++ else chkCount--

                    if (isAllChecked()) (context as Activity).findViewById<Button>(btn_chart).setBackgroundResource(cb_checked)
                    else (context as Activity).findViewById<Button>(btn_chart).setBackgroundResource(cb_unchecked)

                    if (isNothingChecked()) context.findViewById<Button>(btn_delete).setBackgroundResource(ic_trashcan_grey)
                    else context.findViewById<Button>(btn_delete).setBackgroundResource(ic_trashcan_red)
                }
            }
        }
    }
}