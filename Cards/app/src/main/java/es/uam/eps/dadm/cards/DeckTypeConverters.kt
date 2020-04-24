package es.uam.eps.dadm.cards

import androidx.room.TypeConverter


class DeckTypeConverters {
    @TypeConverter
    fun stringToCardList(data: String?): MutableList<Card>? {
        if (data == null) {
            return mutableListOf<Card>()
        }
        var lista : List<String>
        var cardsStr : List<String> = data.split("|")
        var cards : MutableList<Card> = mutableListOf()

        for(cardStr in cardsStr){
            lista = cardStr.toString().split(",")
            cards.add(Card(id=lista[0], question=lista[1], answer=lista[2], date=lista[3],quality=lista[4].toInt(),repetitions=lista[5].toInt(),interval=lista[6].toInt(),nextPracticeDate=lista[7].toInt(),easiness=lista[8].toDouble(),currentDate=lista[9].toInt()))
        }


        return cards
    }

    @TypeConverter
    fun cardListToString(someObjects: List<Card>?): String? {
        var str = ""
        var flag = false

        if (someObjects == null) {
            return ""
        }

        for(card in someObjects){
            if(!flag){
                flag = true
            }else{
                str += "|"
            }

            str += "${card.id},${card.question},${card.answer},${card.date},${card.quality},${card.repetitions},${card.interval},${card.nextPracticeDate},${card.easiness},${card.currentDate}"
        }

        return str
    }
}