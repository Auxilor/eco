package redempt.crunch.token

interface Value : Token {
    fun getValue(variableValues: DoubleArray): Double
    fun getClone(): Value
}