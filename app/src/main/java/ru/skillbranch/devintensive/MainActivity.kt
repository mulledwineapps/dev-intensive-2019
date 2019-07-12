package ru.skillbranch.devintensive

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import ru.skillbranch.devintensive.extensions.hideKeyBoard
import ru.skillbranch.devintensive.utils.KeyboardUtils

class MainActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView

    lateinit var benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //KeyboardUtils.addKeyboardToggleListener(this, object : KeyboardUtils.SoftKeyboardToggleListener {
        //    override fun onToggleSoftKeyboard(isVisible: Boolean) {
        //        Log.d("M_MainActivity", "keyboard visible: $isVisible")
        //    }
        //})

//        benderImage = findViewById(R.id.iv_bender)
        benderImage = iv_bender
        textTxt = tv_text
        messageEt = et_message
        sendBtn = iv_send

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        Log.d("M_MainActivity","onCreate $status $question")

        val(r,g,b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r,g,b), PorterDuff.Mode.MULTIPLY)

        textTxt.text = benderObj.askQuestion()
        sendBtn.setOnClickListener(this)

        // messageEt.imeOptions = EditorInfo.IME_ACTION_DONE
        // messageEt.setSingleLine(true)
        // messageEt.inputType = InputType.TYPE_CLASS_TEXT // android:inputType="text"
        messageEt.setOnEditorActionListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)
        Log.d("M_MainActivity","onSaveInstanceState ${benderObj.status.name} ${benderObj.question.name}")
    }

    private fun sendAnswer() {
        if (benderObj.question != Bender.Question.IDLE) {
            val (phrase, color) = benderObj.listenAnswer(messageEt.text.toString()) //.toLowerCase()
            messageEt.setText("")
            val (r, g, b) = color
            benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
            tv_text.text = phrase
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.iv_send) sendAnswer()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        Log.d("M_MainActivity","OnEditorAction: actionId = $actionId")
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            sendAnswer()
            hideKeyBoard()
        }
        return false
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity","OnRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity","OnStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity","OnResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity","OnPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity","OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity","OnDestroy")
    }

    // ctrl + shift + 1..9 - установить закладку, ctrl + 1..9 - перейти к закладке
    // ctrl + shift + Up/Down arrow - перетащить строку с помощью клавиш
    // kill -9 PID - убить процесс с указанным PID

    // работа с терминалом
    // shell
    // ps - вывести все запущенные процессы
    // | (pipe)- всё, что было стандартным выводом прошлой команды передавать в качестве ввода для следующей команды
    // ps | grep ru.skillbranch.devintensive - вывести только строки, содержащие название пакета
}
