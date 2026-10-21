package com.retrolab.casiofx2200

import kotlin.math.*
import java.util.Locale

/** Functional recreation of the fx-2200. Sequential arithmetic, eight displayed
 * significant digits; Double internally. No ROM or undocumented chip timings. */
class CalculatorEngine {
    var value = 0.0; private set
    var memory = 0.0; private set
    var mode = "RAD"; private set
    var inverse = false; private set
    var hyper = false; private set
    var powered = true; private set
    var error = ""; private set
    var notice = "Preparada. Introduzca el número antes de la función."; private set
    var operation = ""; private set
    val count get() = samples.size
    private var angle = "RAD"
    private var accumulator = 0.0
    private var entry = ""
    private var editing = false
    private var waiting = false
    private var repeatOperation = ""
    private var repeatOperand = 0.0
    private val samples = mutableListOf<Double>()
    private var fraction = mutableListOf<Double>()
    private var dms = mutableListOf<Double>()
    private var special = ""

    fun format(v: Double): String {
        if (v == 0.0) return "0"
        val rounded = String.format(Locale.US, "%.8g", v).toDouble()
        if (abs(rounded) >= 1e8 || abs(rounded) < 1e-7) {
            val parts = String.format(Locale.US, "%.7e", rounded).split('e')
            return parts[0].trimEnd('0').trimEnd('.') + " E" + parts[1].toInt().let { if(it>=0) "+$it" else "$it" }
        }
        return String.format(Locale.US, "%.${(7-floor(log10(abs(rounded))).toInt()).coerceIn(0,14)}f", rounded)
            .let { if(it.contains('.')) it.trimEnd('0').trimEnd('.') else it }
    }
    fun display(): String = when {
        !powered -> ""
        error.isNotEmpty() -> "Error"
        special.isNotEmpty() -> special
        editing -> entry.replace("e", " E")
        else -> format(value)
    }
    fun setMode(next: String) {
        require(next in listOf("RAD", "DEG", "GRA", "SD"))
        commit()
        mode = next
        if (next != "SD") angle = next
        inverse = false; hyper = false
        notice = if(next == "SD") "SD: M+ registra un dato. INV M+ lo retira." else "Unidad angular: $next."
    }
    private fun checked(v: Double): Double {
        require(v.isFinite() && abs(v) < 1e100) { "Desbordamiento o argumento fuera de dominio." }
        return if(abs(v)<1e-99) 0.0 else if(v==0.0) 0.0 else v
    }
    private fun current(): Double {
        if(!editing) return value
        val normalized = if(entry.endsWith("e") || entry.endsWith("e-") || entry.endsWith("e+")) entry+"0" else entry
        return checked(normalized.toDoubleOrNull() ?: throw IllegalArgumentException("Entrada incompleta."))
    }
    private fun commit() {
        if(editing) value=current()
        editing=false
        if(fraction.isNotEmpty()) {
            require(value!=0.0) { "El denominador no puede ser cero." }
            value=checked(if(fraction.size==1) fraction[0]/value else {
                val sign=if(fraction[0]<0) -1 else 1
                sign*(abs(fraction[0])+fraction[1]/value)
            })
            fraction.clear()
        }
        if(dms.isNotEmpty()) {
            val pieces=dms+value
            require(pieces.drop(1).all { it>=0 && it<60 }) { "Minutos y segundos deben ser menores de 60." }
            val sign=if(pieces[0]<0) -1 else 1
            value=sign*(abs(pieces[0])+pieces.getOrElse(1){0.0}/60+pieces.getOrElse(2){0.0}/3600)
            dms.clear()
        }
        special=""
    }
    private fun begin() {
        if(!editing) { entry="0"; editing=true; special="" }
        if(waiting) waiting=false
        repeatOperation=""
    }
    private fun digit(key: String) {
        begin()
        if(entry.contains('e')) {
            if(key==".") return
            val split=entry.indexOf('e'); val exponent=entry.substring(split+1)
            if(exponent.count { it.isDigit() }>=2) return
            entry+=key
        } else {
            if(key==".") { if(!entry.contains('.')) entry+="."; return }
            if(entry.count { it.isDigit() }>=8 && entry!="0" && entry!="-0") return
            entry=when(entry) { "0" -> key; "-0" -> "-$key"; else -> entry+key }
        }
        value=current()
    }
    private fun calculate(a: Double, op: String, b: Double): Double = checked(when(op) {
        "+" -> a+b; "-" -> a-b; "*" -> a*b
        "/" -> { require(b!=0.0) { "División por cero." }; a/b }
        "POW" -> { require(a>=0 || b==floor(b)) { "Potencia sin resultado real." }; a.pow(b) }
        "ROOT" -> {
            require(b!=0.0) { "Índice de raíz igual a cero." }
            if(a<0) { require(b==floor(b) && abs(b%2)==1.0) { "Raíz sin resultado real." }; -(-a).pow(1/b) } else a.pow(1/b)
        }
        else -> b
    })
    private fun operator(op: String) {
        if(waiting) { operation=op; inverse=false; hyper=false; return }
        commit()
        if(operation.isNotEmpty()) value=calculate(accumulator,operation,value)
        accumulator=value; operation=op; waiting=true; repeatOperation=""
        inverse=false; hyper=false; notice="Operación pendiente: ${symbol(op)}."
    }
    private fun equals() {
        commit()
        if(operation.isNotEmpty()) {
            val b=if(waiting) accumulator else value
            repeatOperand=b; repeatOperation=operation
            value=calculate(accumulator,operation,b)
            operation=""
        } else if(repeatOperation.isNotEmpty()) value=calculate(value,repeatOperation,repeatOperand)
        waiting=false; notice="Resultado: ${format(value)}."
    }
    private fun radians(v: Double) = when(angle) { "DEG" -> v*PI/180; "GRA" -> v*PI/200; else -> v }
    private fun angular(v: Double) = when(angle) { "DEG" -> v*180/PI; "GRA" -> v*200/PI; else -> v }
    private fun unary(key: String) {
        commit()
        val v=value; val inv=inverse; val hyp=hyper
        val result=when(key) {
            "SIN", "COS", "TAN" -> if(hyp) {
                if(inv) when(key) {
                    "SIN" -> asinh(v); "COS" -> { require(v>=1) { "arcosh requiere x ≥ 1." }; acosh(v) }
                    else -> { require(abs(v)<1) { "artanh requiere |x| < 1." }; atanh(v) }
                } else when(key) { "SIN" -> sinh(v); "COS" -> cosh(v); else -> tanh(v) }
            } else if(inv) {
                when(key) {
                    "SIN" -> { require(abs(v)<=1) { "arcsin requiere |x| ≤ 1." }; angular(asin(v)) }
                    "COS" -> { require(abs(v)<=1) { "arccos requiere |x| ≤ 1." }; angular(acos(v)) }
                    else -> angular(atan(v))
                }
            } else {
                require(abs(radians(v))<1e8*PI/180) { "Ángulo fuera de rango." }
                when(key) {
                    "SIN" -> sin(radians(v)); "COS" -> cos(radians(v))
                    else -> { require(abs(cos(radians(v)))>1e-14) { "Tangente indefinida." }; tan(radians(v)) }
                }.let { if(abs(it)<1e-14) 0.0 else it }
            }
            "LOG" -> if(inv) 10.0.pow(v) else { require(v>0) { "log requiere x > 0." }; log10(v) }
            "LN" -> if(inv) exp(v) else { require(v>0) { "ln requiere x > 0." }; ln(v) }
            "SQRT" -> { require(v>=0) { "Raíz sin resultado real." }; sqrt(v) }
            "RECIP" -> { require(v!=0.0) { "División por cero." }; 1/v }
            "FACT" -> { require(v>=0 && v==floor(v) && v<=69) { "Factorial: entero entre 0 y 69." }; (2..v.toInt()).fold(1.0){a,n->a*n} }
            else -> v
        }
        value=checked(result); inverse=false; hyper=false; waiting=false
        repeatOperation=""; notice="Resultado: ${format(value)}."
    }
    private fun statistics(key: String) {
        commit()
        val n=samples.size; val mean=if(n==0) 0.0 else samples.average()
        value=checked(when(key) {
            "FRAC" -> samples.sumOf { it*it }
            "RECIP" -> samples.sum()
            "FACT" -> n.toDouble()
            "SQRT" -> { require(n>0) { "No hay datos registrados." }; mean }
            "MIN", "MR" -> {
                val divisor=if(key=="MIN") n-1 else n
                require(divisor>0) { "Datos insuficientes para la desviación." }
                sqrt(samples.sumOf { (it-mean).pow(2) }/divisor)
            }
            else -> value
        })
        inverse=false; waiting=false; repeatOperation=""; notice="SD: $n datos. Resultado: ${format(value)}."
    }
    private fun clear(all: Boolean) {
        value=0.0; entry=""; editing=false; special=""; fraction.clear(); dms.clear()
        error=""; inverse=false; hyper=false
        if(all) { accumulator=0.0; operation=""; waiting=false; repeatOperation="" }
        else waiting=false
        notice=if(all) "Cálculo borrado. Memoria conservada." else "Entrada borrada."
    }
    fun press(key: String) {
        if(key=="POWER") { powered=!powered; if(powered) clear(true); return }
        if(!powered) return
        if(key=="AC") { clear(true); return }
        if(key=="C") { clear(false); return }
        if(error.isNotEmpty()) return
        try {
            when {
                key.length==1 && (key[0].isDigit() || key==".") -> digit(key)
                key in listOf("+","-","*","/") -> operator(key)
                key=="POW" -> operator(if(inverse) "ROOT" else "POW")
                key=="=" -> equals()
                key=="INV" -> { inverse=!inverse; notice=if(inverse) "INV: función secundaria." else "Función principal." }
                key=="HYP" -> { hyper=!hyper; notice=if(hyper) "Función hiperbólica." else "Función circular." }
                key=="MODE" -> setMode(listOf("RAD","DEG","GRA","SD")[(listOf("RAD","DEG","GRA","SD").indexOf(mode)+1)%4])
                key=="EXP" -> {
                    if(inverse) { value=PI; editing=false; special=""; waiting=false; inverse=false }
                    else { begin(); if(!entry.contains('e')) { if(entry=="0") entry="1"; entry+="e" } }
                }
                key=="SIGN" -> {
                    if(editing && entry.contains('e')) {
                        val p=entry.indexOf('e')+1
                        entry=entry.substring(0,p)+(if(entry.substring(p).startsWith('-')) entry.substring(p+1) else "-"+entry.substring(p))
                    } else if(editing) entry=if(entry.startsWith('-')) entry.drop(1) else "-$entry"
                    else { value=-value; special=""; waiting=false }
                }
                key=="BACKSPACE" -> { if(editing) { entry=entry.dropLast(1); if(entry=="" || entry=="-") entry="0"; value=current() } }
                key=="FRAC" && mode!="SD" -> {
                    if(inverse) { commit(); inverse=false; notice="Fracción convertida a decimal." }
                    else {
                        require(fraction.size<2) { "Use = para terminar la fracción." }
                        fraction.add(current()); editing=false; value=0.0
                        special=fraction.joinToString("┘"){format(it)}+"┘"; waiting=false
                        notice=if(fraction.size==1) "Introduzca denominador; otra pulsación permite número mixto." else "Introduzca el denominador y pulse =."
                    }
                }
                key=="DMS" -> {
                    if(inverse) {
                        commit(); val v=abs(value)
                        require(v<1e8) { "Ángulo demasiado grande para notación sexagesimal." }
                        var deg=floor(v); var minutes=floor((v-deg)*60)
                        var seconds=round(((v-deg)*60-minutes)*60*10000)/10000
                        if(seconds>=60) { seconds=0.0; minutes+=1 }
                        if(minutes>=60) { minutes=0.0; deg+=1 }
                        special=(if(value<0) "−" else "")+"${deg.toInt()}°${minutes.toInt()}′${format(seconds)}″"
                        inverse=false; notice="Notación sexagesimal."
                    } else {
                        dms.add(current()); editing=false; value=0.0; special=dms.joinToString("°"){format(it)}+"°"
                        if(dms.size==3) {
                            val p=dms.toList(); require(p[1]>=0 && p[1]<60 && p[2]>=0 && p[2]<60) { "Minutos o segundos fuera de rango." }
                            value=(if(p[0]<0) -1 else 1)*(abs(p[0])+p[1]/60+p[2]/3600); dms.clear(); special=""
                        }
                        waiting=false; notice="Entrada de grados, minutos y segundos."
                    }
                }
                mode=="SD" && key in listOf("FRAC","RECIP","FACT","SQRT","MIN","MR") -> statistics(key)
                key=="MIN" -> { commit(); memory=value; waiting=false; inverse=false; notice="Valor guardado en M." }
                key=="MR" -> { value=memory; editing=false; special=""; waiting=false; inverse=false; notice="Memoria recuperada." }
                key=="MPLUS" -> {
                    if(mode=="SD") {
                        commit()
                        if(inverse) { val i=samples.indexOfLast { it==value }; require(i>=0) { "Ese dato no está registrado." }; samples.removeAt(i) }
                        else { require(samples.size<10000) { "Capacidad de datos agotada." }; samples.add(value) }
                        notice="SD: ${samples.size} datos registrados."
                    } else { if(operation.isNotEmpty() && !waiting) equals() else commit(); memory=checked(memory+value); notice="Valor sumado a M." }
                    editing=false; inverse=false; waiting=false
                }
                key=="SAC" -> { samples.clear(); clear(true); notice="Registros estadísticos borrados." }
                key=="MC" -> { memory=0.0; notice="Memoria M borrada." }
                key in listOf("SIN","COS","TAN","LOG","LN","SQRT","RECIP","FACT") -> unary(key)
                else -> throw IllegalArgumentException("Tecla desconocida: $key")
            }
        } catch(e: IllegalArgumentException) {
            error=e.message ?: "Error de cálculo."; notice=error; inverse=false; hyper=false
        }
    }
    fun pendingDisplay() = if(operation.isNotEmpty()) "${format(accumulator)} ${symbol(operation)}" else ""
    private fun symbol(op: String) = when(op) { "*" -> "×"; "/" -> "÷"; "POW" -> "xʸ"; "ROOT" -> "ʸ√x"; else -> op }
}
