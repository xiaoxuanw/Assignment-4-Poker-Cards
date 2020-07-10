package com.example.cse438.cse438_assignment4

class Player {
    // we need to store the number of win and lose for each player
    private var win = 0
    private var lose = 0
    private var score = 0
    private var cards = ArrayList<Card>()
    private var userName = ""

    // get win times
    public fun getWinTimes(): Int {
        return win
    }

    public fun getUsername(): String {
        return userName;
    }
    // get lose times
    public fun getLoseTimes(): Int {
        return lose
    }

    // get score
    public fun getScore(): Int {
        return score
    }

    // calculate score
    private fun calculateScore(newScore: Int): Int {
        println("test new score: " + newScore)

        if (newScore == 1) {
            if (score + 11 > 21) {
                // busted, we should use it as 1
                score++
            } else {
                // we should use it as 11
                score += 11
            }
        } else {
            score += newScore
        }
        println("score: " + score)
        return score
    }

    // add a new card
    public fun addCard(card: Card) {
        cards.add(card)
        calculateScore(card.getCardValue())
    }

    // add one win
    public fun addWin() {
        win++
    }

    // add one lose
    public fun addLose() {
        lose++
    }

    fun assignValue(key: String, value: Any) {
        when (key) {
            "username" -> this.userName = (value as String)
            "losses" -> this.lose = (value as Long).toInt()
            "wins" -> this.win = (value as Long).toInt()
        }
    }
}