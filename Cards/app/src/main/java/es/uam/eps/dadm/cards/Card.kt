/***
 * Fichero que contiene la clase Card con toda la información relativa
 * a las tarjetas y las funciones necesarias para gestionarlas (back-end).
 */
package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Math.max
import java.lang.Math.round
import java.util.*

@Entity
open class Card(@PrimaryKey var id: String = UUID.randomUUID().toString(), var question: String = "", var answer: String = "", var date: String = Date().toString(), var quality : Int = -1,var repetitions : Int = 0, var interval : Int = 1, var nextPracticeDate : Int = 1, var easiness : Double = 2.5, var currentDate : Int = 0) {


    override fun equals(other: Any?): Boolean {
        if(other !is Card){
            return false
        }
        return ((this.answer.toLowerCase() == other.answer.toLowerCase()) && (this.question.toLowerCase() == other.question.toLowerCase()))
    }

    override fun hashCode(): Int {
        return question.toLowerCase().hashCode() + answer.toLowerCase().hashCode()
    }

    override fun toString(): String {
        return "Card(question = $question,answer = $answer)"
    }

    companion object {
        fun createCard() : Card {
            var card : Card
            print("Teclea el tipo 0 (Card) 1 (Cloze): ")
            var tipo = readLine()?.toIntOrNull()
            while(tipo == null || (tipo != 0 && tipo != 1)){
                print("Teclea el tipo 0 (Card) 1 (Cloze): ")
                tipo = readLine()?.toIntOrNull()
            }

            print("\tTeclea la pregunta: ")
            var pregunta = readLine()
            while(pregunta == null){
                print("\nTeclea la pregunta otra vez:")
                pregunta = readLine()
            }

            print("\tTeclea la respuesta: ")
            var respuesta = readLine()
            while(respuesta == null){
                print("\nTeclea la respuesta otra vez:")
                respuesta = readLine()
            }

            if(tipo == 0)
                card = Card(pregunta.orEmpty(),respuesta.orEmpty())
            else
                card = Cloze(pregunta.orEmpty(),respuesta.orEmpty())

            return card
        }
    }

    open fun show(){
        quality = -1
        var tempQ : Int?
        println(question)
        print("\tINTRO para ver respuesta:")
        readLine()
        println("\t$answer")
        while(quality != 0 && quality != 3 && quality != 5) {
            print("\tTeclea 0 (Difícil) 3 (Dudo) 5 (Fácil): ")
            tempQ = readLine()?.toIntOrNull()
            if(tempQ != null){
                quality = tempQ
            }
        }
    }

    fun update(){
        easiness = max(easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02),1.3)

        repetitions = if(quality < 3){
            0
        }else{
            repetitions + 1
        }

        interval = when {
            repetitions <= 1 -> {
                1
            }
            repetitions == 2 -> {
                6
            }
            else -> {
                round(interval * easiness).toInt()
            }
        }


        nextPracticeDate = currentDate + interval
    }

    /*fun details(){
        println("\t$question ($answer) eas = ${"%.1f".format(easiness)} rep = $repetitions int = $interval next = $nextPracticeDate")
    }*/
}

class Cloze(question: String, answer : String,date : String = Date().toString()) : Card(question = question, answer = answer,date = date) {

    fun leer() : String{
        val listaStr = question.split("*")
        var retorno = ""
        var par = false

        for(str in listaStr){
            retorno += if(par){
                answer
            }else{
                str
            }
            par = !par
        }

        return retorno
    }

    override fun show() {
        quality = -1
        var tempQ : Int?
        println(question)
        print("\tINTRO para ver respuesta:")
        readLine()
        println("\t${leer()}")
        while(quality != 0 && quality != 3 && quality != 5) {
            print("\tTeclea 0 (Difícil) 3 (Dudo) 5 (Fácil): ")
            tempQ = readLine()?.toIntOrNull()
            if(tempQ != null){
                quality = tempQ
            }
        }
    }
}

/*object Simulacion{
    fun simularTarjetas(lista : List<Card>, dias : Int){
        var i = 0
        var dificultad = listOf(0,3,5)
        var cadena : String

        if(lista.size <= 0){
            println("La lista de tarjetas esta vacía.")
            return
        }

        while(i < dias){
            println("Fecha: $i")

            for(tarjeta in lista) {
                if (i == 0 || tarjeta.nextPracticeDate == i) {

                    tarjeta.quality = dificultad[nextInt(0,3)]
                    tarjeta.currentDate = i
                    tarjeta.update()
                    cadena = "\tTarjeta ${lista.indexOf(tarjeta) + 1}:\n\t"
                    cadena += when(tarjeta.quality){
                        0 -> "\tDifícil (Difficult) "
                        3 -> "\tDuda (Doubt) "
                        5 -> "\tFácil (Easy) "
                        else -> "\tDesconocido "
                    }
                    cadena += "eas = ${"%.1f".format(tarjeta.easiness)} rep = ${tarjeta.repetitions} int = ${tarjeta.interval} next = ${tarjeta.nextPracticeDate}"
                    println(cadena)
                }
            }

            i++
        }
        return
    }
}*/