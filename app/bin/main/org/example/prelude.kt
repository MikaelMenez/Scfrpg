import kotlin.math.floor

infix fun Int.div(outro: Int): Int = floor(this.toFloat() / outro.toFloat()).toInt()
