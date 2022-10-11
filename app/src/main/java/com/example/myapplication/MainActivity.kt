package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity() : AppCompatActivity() {

    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()
    var currentCardDisplayedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Variables
        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val intent = Intent(this, AddCardActivity::class.java)
        val nextButton = findViewById<ImageView>(R.id.Next_Arrow)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        if (allFlashcards.size > 0)
        {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }


        //Setup
        flashcardQuestion.visibility = View.VISIBLE
        flashcardAnswer.visibility = View.INVISIBLE

        //Click from question to answer
        flashcardQuestion.setOnClickListener {
            flashcardQuestion.visibility = View.INVISIBLE
            flashcardAnswer.visibility = View.VISIBLE
        }

        //Click from answer to question
        flashcardAnswer.setOnClickListener {
            flashcardQuestion.visibility = View.VISIBLE
            flashcardAnswer.visibility = View.INVISIBLE
        }


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (data != null) {
                val string1 = data.getStringExtra("Question_Key")
                val string2 = data.getStringExtra("Answer_Key")

                flashcardQuestion.text = string1
                flashcardAnswer.text = string2

                // Log the value of the strings for easier debugging
                Log.i("MainActivity", "string1: $string1")
                Log.i("MainActivity", "string2: $string2")

                //Display newly created flashcard
                flashcardQuestion.text = string1
                flashcardAnswer.text = string2

                if (string1 != null && string2 != null)
                {
                    //Save to database
                    flashcardDatabase.insertCard(Flashcard(string1, string2))

                    //Update set of flashcards
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                }

            } else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }

        findViewById<ImageView>(R.id.AddButton).setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }

        nextButton.setOnClickListener {
            if (allFlashcards.size == 0)
            {
                return@setOnClickListener
            }

            currentCardDisplayedIndex++

            if(currentCardDisplayedIndex >= allFlashcards.size)
            {
                Snackbar.make(
                    flashcardQuestion,
                    "You've reached the end of the cards, going back to start.",
                    Snackbar.LENGTH_SHORT)
                    .show()
                    currentCardDisplayedIndex = 0
            }

            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val (question, answer) = allFlashcards[currentCardDisplayedIndex]

            flashcardAnswer.text = answer
            flashcardQuestion.text = question
        }


        }
    }