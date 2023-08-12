package com.example.alertdialog

class ItemsSingleChoice(
    val values: List<Int>,
    val currentIndex: Int
) {
    companion object{
        fun createItems(currentValue: Int): ItemsSingleChoice{
            val values = (0..10)
            val currentIndex = values.indexOf(currentValue)
            return if (currentIndex == -1) {
                val list = values + currentValue
                ItemsSingleChoice(list, list.lastIndex)
            } else {
                ItemsSingleChoice(values.toList(), currentIndex)
            }
        }
    }
}