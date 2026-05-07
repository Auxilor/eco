package redempt.crunch.data

object FastNumberParsing {
    @JvmStatic
    fun parseInt(input: String): Int = parseInt(input, 0, input.length)

    @JvmStatic
    fun parseInt(input: String, start: Int, end: Int): Int {
        if (start == end) throw NumberFormatException("Zero-length input")
        var i = start
        var negative = false
        if (input[i] == '-') {
            negative = true
            i++
        }
        var output = 0
        while (i < end) {
            val c = input[i]
            if (c > '9' || c < '0') throw NumberFormatException("Non-numeric character in input '${input.substring(start, end)}'")
            output *= 10
            output += c - '0'
            i++
        }
        return if (negative) -output else output
    }

    @JvmStatic
    fun parseDouble(input: String): Double = parseDouble(input, 0, input.length)

    @JvmStatic
    fun parseDouble(input: String, start: Int, end: Int): Double {
        if (start == end) throw NumberFormatException("Zero-length input")
        var i = start
        var negative = false
        if (input[start] == '-') {
            negative = true
            i++
        }
        var output = 0.0
        while (i < end) {
            val c = input[i]
            if (c == '.') {
                i++
                break
            }
            if (c > '9' || c < '0') throw NumberFormatException("Non-numeric character in input '$input'")
            output = output * 10.0 + (c - '0')
            i++
        }
        var divisor = 1.0
        while (i < end) {
            val c = input[i]
            if (c == '.') throw NumberFormatException("Second period in double for input '$input'")
            if (c > '9' || c < '0') throw NumberFormatException("Non-numeric character in input '$input'")
            output = output * 10.0 + (c - '0')
            divisor *= 10.0
            i++
        }
        if (divisor > 1.0) output /= divisor
        return if (negative) -output else output
    }
}