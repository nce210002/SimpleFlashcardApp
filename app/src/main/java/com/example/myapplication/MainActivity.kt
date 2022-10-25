package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

            val answerSideView = flashcardAnswer

            // get the center for the clipping circle

            // get the center for the clipping circle
            val cx = answerSideView.width / 2
            val cy = answerSideView.height / 2

            // get the final radius for the clipping circle

            // get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius)

            // hide the question and show the answer to prepare for playing the animation!

            // hide the question and show the answer to prepare for playing the animation!
            flashcardQuestion.visibility = View.INVISIBLE
            flashcardAnswer.visibility = View.VISIBLE

            anim.setDuration(1000)
            anim.start()
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
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        nextButton.setOnClickListener {

            if (allFlashcards.size == 0)
            {
                return@setOnClickListener
            }

            // Resource animation files
            val leftOutAnim = AnimationUtils.loadAnimation(it.getContext(), R.anim.left_out)
            val rightInAnim = AnimationUtils.loadAnimation(it.getContext(), R.anim.right_in)

            leftOutAnim.setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // this method is called when the animation first start
                    flashcardAnswer.visibility = View.INVISIBLE
                    flashcardQuestion.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // this method is called when the animation is finished playing
                    flashcardQuestion.startAnimation(rightInAnim)

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

                    flashcardAnswer.visibility = View.INVISIBLE
                    flashcardQuestion.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // we don't need to worry about this method
                }
            })

            flashcardQuestion.startAnimation(leftOutAnim)
        }


        }
    }