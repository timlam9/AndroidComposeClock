package com.lamti.androidcomposeclock

enum class Turn {

    Player,
    Opponent

}

data class Box(val turn: Turn?)
