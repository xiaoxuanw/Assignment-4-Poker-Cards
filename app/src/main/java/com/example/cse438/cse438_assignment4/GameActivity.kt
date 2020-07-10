package com.example.cse438.cse438_assignment4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.cse438.cse438_assignment4.util.CardRandomizer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList

class GameActivity: AppCompatActivity() {
    //Instance variables
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val documentReference = FirebaseFirestore.getInstance().document("players/${firebaseAuth.currentUser?.uid}")
    private lateinit var gestureDetector: GestureDetectorCompat
    lateinit var cardZero : ImageView
    public lateinit var cardOne : ImageView
    public lateinit var cardTwo: ImageView
    public lateinit var cardThree: ImageView
    private var playerCardsViews = ArrayList<ImageView>()
    private var dealerCardsViews = ArrayList<ImageView>()
    private val player = Player()
    private val dealer = Dealer()
    private var cardList = ArrayList<Card>()
    private var cardListId = ArrayList<Int>()
    private var gameResult = 0 // 1 for win, 2 for tie, 3 for lose
    private var playerIndex = 0
    private var dealerIndex = 0
    private var hiddenId = 0
    private var gameOver: Boolean = false
    private var playerTurn: Boolean = true
    var chipNum:Int =0
    var newBet:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //set the lateinits
        gestureDetector = GestureDetectorCompat(this, MyGestureListener())
        cardList = ArrayList()
        cardListId = ArrayList()
        playerCardsViews = arrayListOf(playerNewOne, playerNewTwo, playerNewThree, playerNewFour, playerNewFive, playerNewSix)
        dealerCardsViews = arrayListOf(dealerNewOne, dealerNewTwo,dealerNewThree,dealerNewFour,dealerNewFive,dealerNewSix)

        //Display the initial values from database
        displayValues()

        println("Cardlistid:" + cardListId)
        // add event listener to log out button
        logout_button.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        //placeBet listener
        placeBetButton.setOnClickListener {
            enterBet()        }

        //slick listener to leaders
        leaders_button.setOnClickListener {
            var intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        //click listener to restart the game
        newGameButton.setOnClickListener {
            finish()
            startActivity(intent)
        }

        // transfer ID to cards
        for (i in cardListId) {
            // find the name of the card
            cardList.add(Card(dealCard()))
        }
    }

    override fun onResume() {
        super.onResume()

        //set the lateinits
        gestureDetector = GestureDetectorCompat(this, MyGestureListener())
        gameOver = false
        cardList = ArrayList()
        cardListId = ArrayList()
        playerCardsViews = arrayListOf(playerNewOne, playerNewTwo, playerNewThree, playerNewFour, playerNewFive, playerNewSix)
        dealerCardsViews = arrayListOf(dealerNewOne, dealerNewTwo,dealerNewThree,dealerNewFour,dealerNewFive,dealerNewSix)

        //Set initial views and display values
        setInitialCardView()
        displayValues()

        // transfer ID to cards
        for (i in cardListId) {
            // find the name of the card
            cardList.add(Card(dealCard()))
        }

    }
    // deal with gesture
    private inner class MyGestureListener: GestureDetector.SimpleOnGestureListener() {
        private var swipedDistance = 150
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stand()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            // we want our motion to swipe to the right
            if (e2.x - e1.x > swipedDistance) {
                hit()
                return true
            }
            return false
        }
    }

    private fun stand() {
        //If the player stands, it's the dealer's turn
        playerTurn = false
        if (!gameOver) {
            while(dealer.getScore() < 17) {
                val rand = Random().nextInt(cardList.size)
                val nextCard = cardList[rand]
                dealer.addCard(nextCard)
                dealerCardsViews[dealerIndex++].setImageResource(cardListId[rand])
                cardList.removeAt(rand)
                cardListId.removeAt(rand)
            }
            determineGame()
        } else {
            dealerCardsViews[0].setImageResource(hiddenId)
            Toast.makeText(this, "Please start a new game!", Toast.LENGTH_LONG).show()
        }
    }

    private fun hit() {
        if(!gameOver && playerTurn){
            // add card to player
            val rand = Random().nextInt(cardList.size)
            val nextCard = cardList[rand]
            player.addCard(nextCard)

            // update layout
            playerCardsViews[playerIndex++].setImageResource(cardListId[rand])

            //Remove cards to avoid repeated cards
            cardList.removeAt(rand)
            cardListId.removeAt(rand)

            //determine if game is over
            if (player.getScore() > 21) {
                result_text.text = "Game Result: Player Losses!"

                //Reset the instance variables
                gameOver = true
                gameResult = 3

                //Show hidden card
                card0.setImageResource(hiddenId)
                //add
                addToDataBase()
            }
        } else if (gameOver){
            Toast.makeText(this, "Please start a new game!", Toast.LENGTH_LONG).show()
        } else if (!playerTurn){
            Toast.makeText(this, "Dealer's turn!", Toast.LENGTH_LONG).show()
        }
    }

    //A helper method to determine if the game is won
    private fun determineGame(){
        if(!gameOver){
            if (dealer.getScore() > 21) {
                result_text.text = "Game Result: Player Wins!"

                card0.setImageResource(hiddenId)
                //Reset the instance variables
                gameOver = true
                gameResult = 1
            } else if (dealer.getScore() > 17) {
                if(player.getScore() > dealer.getScore()){
                    result_text.text = "Game Result: Player Wins!"

                    card0.setImageResource(hiddenId)
                    //Reset the instance variables
                    gameOver = true
                    gameResult = 1
                } else if (player.getScore() < dealer.getScore()){
                    result_text.text = "Game Result: Player Losses!"

                    card0.setImageResource(hiddenId)
                    //Reset the instance variables
                    gameOver = true
                    gameResult = 3
                } else {
                    result_text.text = "Game Result: Tie!"

                    card0.setImageResource(hiddenId)
                    gameOver = true
                    gameResult = 2
                }
            } else {
                gameOver = false
            }
            // save to DB
            addToDataBase()
        }
    }
    //A function that adds all the results to the database
    private fun addToDataBase() {
        //Add wins and losses based on the result
        if(gameResult==1){
            // win
            addChips()
            player.addWin()
            documentReference.get()
                .addOnSuccessListener {
                    val totalWin = when(it.contains("wins")) {
                        true -> { it.get("wins", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentReference.update("wins", totalWin)
                        .addOnSuccessListener {
                            displayValues()
                        }
                }
            displayValues()
        }else if (gameResult==3){
            player.addLose()
            documentReference.get()
                .addOnSuccessListener {
                    val totalLoss = when(it.contains("losses")) {
                        true -> { it.get("losses", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentReference.update("losses", totalLoss)
                        .addOnSuccessListener {
                            displayValues()
                        }
                }
        }
    }

    //A helper method that display the player's info on the screen
    private fun displayValues() {
        documentReference.get().addOnSuccessListener {
            if(it==null){
                win_text.text="0";
                lose_text.text="0"
                chips_text.text="0"
                player_name.text="Invalid"
            } else {
                win_text.text = it.get("wins").toString()
                lose_text.text= it.get("losses").toString()
                chips_text.text=it.get("chips").toString()
                player_name.text=it.get("username").toString()
            }
        }
    }

    private fun enterBet(){
        newBet = Integer.parseInt(betAmount.text.toString())
        chipNum=Integer.parseInt(chips_text.text.toString())

        if(newBet > chipNum){
            Toast.makeText(this, "You don't have this much!", Toast.LENGTH_LONG).show()
        } else {
            documentReference.get()
                .addOnSuccessListener {
                    val totalChips = when(it.contains("chips")) {
                        true -> { it.get("chips", Long::class.java) as Long - newBet}
                        false -> 1
                    }

                    documentReference.update("chips", totalChips)
                        .addOnSuccessListener {
                            displayValues()
                        }
                }
            displayValues()
        }
    }

    private fun addChips(){
        documentReference.get()
            .addOnSuccessListener {
                val totalChips = when(it.contains("chips")) {
                    true -> { it.get("chips", Long::class.java) as Long + 2*newBet}
                    false -> 1
                }

                documentReference.update("chips", totalChips)
                    .addOnSuccessListener {
                        displayValues()
                    }
            }
        displayValues()
    }

    private fun setInitialCardView(){
        //set card view
        cardZero = card0
        cardOne = card1
        cardTwo = card2
        cardThree = card3

        //cardNames
        val cardOneName = dealCard()
        val cardTwoName = dealCard()
        val cardThreeName = dealCard()
        val cardHiddenName = dealCard()
        //set the resources
        var imageIdOne: Int = this.resources.getIdentifier(cardOneName,"drawable",this.getPackageName())
        var imageIdTwo: Int = this.resources.getIdentifier(cardTwoName,"drawable",this.getPackageName())
        var imageIdThree: Int = this.resources.getIdentifier(cardThreeName,"drawable",this.getPackageName())
        var imageIdHidden: Int = this.resources.getIdentifier(cardHiddenName,"drawable",this.getPackageName())

        //Add the card to the dealer and the player
        dealer.addCard(Card(cardOneName))
        dealer.addCard(Card(cardHiddenName))
        player.addCard(Card(cardTwoName))
        player.addCard(Card(cardThreeName))

        //test
        println("dealer: " + dealer.getScore() + " player: " + player.getScore())
        cardOne.setImageResource(imageIdOne)
        cardTwo.setImageResource(imageIdTwo)
        cardThree.setImageResource(imageIdThree)

        //change the hidden id
        hiddenId = imageIdHidden
        //cardZero.setImageResource(imageIdHidden)

        //If the dealer has 21 he automatically wins
        if(dealer.getScore() == 21){
            gameOver = true
            gameResult = 3
            addToDataBase()
            card0.setImageResource(hiddenId)
            Toast.makeText(this, "Please start a new game!", Toast.LENGTH_LONG).show()
        }
    }

    private fun dealCard():String{
        val randomizer: CardRandomizer = CardRandomizer()
        cardListId = randomizer.getIDs(this) as ArrayList<Int>
        val rand: Random = Random()
        val r: Int = rand.nextInt(cardListId.size)
        val id: Int = cardListId.get(r)
        val cardName: String = resources.getResourceEntryName(id)

        return cardName
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}
