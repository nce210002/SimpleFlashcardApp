package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView

class AddCardActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        //Variables
        val cancel_button = findViewById<ImageView>(R.id.CancelButton)
        val save_button = findViewById<ImageView>(R.id.SaveButton)
        val Question_Text = findViewById<EditText>(R.id.question_edit_text).text.toString()
        val Answer_Text = findViewById<EditText>(R.id.answer_edit_text).text.toString()



        //Click save button to save the data
        save_button.setOnClickListener {
            val data = Intent()
            data.putExtra("Question_Key", Question_Text)
            data.putExtra("Answer_Key", Answer_Text)
            setResult(RESULT_OK, data)
            finish()

        }

        //Click cancel button to dismiss back to main
        cancel_button.setOnClickListener{
        finish()
        }
    }
}