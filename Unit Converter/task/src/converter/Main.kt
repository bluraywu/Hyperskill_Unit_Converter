package converter

enum class UnitType(val s: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    NaU("NaU");

    override fun toString(): String {
        return s
    }
}

enum class Unit(
        val simple: String,
        val singular: String,
        val plural: String,
        val multiplier: Double,
        val unitType: UnitType
) {
    METER("m", "meter", "meters", 1.0, UnitType.LENGTH),
    KILOMETER("k", "kilometer", "kilometers", 1000.0, UnitType.LENGTH),
    CENTIMETER("cm", "centimeter", "centimeters", 0.01, UnitType.LENGTH),
    MILLIMETER("mm", "millimeter", "millimeters", 0.001, UnitType.LENGTH),
    MILE("m", "mile", "miles", 1609.35, UnitType.LENGTH),
    YARD("yr", "yard", "yards", 0.9144, UnitType.LENGTH),
    FOOT("ft", "foot", "feet", 0.3048, UnitType.LENGTH),
    INCH("in", "inch", "inches", 0.0254, UnitType.LENGTH),
    GRAM("g", "gram", "grams", 1.0, UnitType.WEIGHT),
    KILOGRAM("km", "kilogram", "kilograms", 1000.0, UnitType.WEIGHT),
    MILLIGRAM("mg", "milligram", "milligrams", 0.001, UnitType.WEIGHT),
    POUND("lb", "pound", "pounds", 453.592, UnitType.WEIGHT),
    OUNCE("oz", "ounce", "ounces", 28.3495, UnitType.WEIGHT),
    KELVIN("c", "kelvin", "kelvins", 0.0, UnitType.TEMPERATURE),
    FAHRENHEIT("f", "degree Fahrenheit", "degrees Fahrenheit", 0.0, UnitType.TEMPERATURE),
    CELSIUS("c", "degree Celsius", "degrees Celsius", 0.0, UnitType.TEMPERATURE),
    NaU("???", "???", "???", 0.0, UnitType.NaU);

    fun getPrintUnit(value: Double): String {
        return if (value == 1.0) singular else plural
    }

    companion object {
        fun getByName(name: String): Unit {
            return when (name.toLowerCase()) {
                "m", "meter", "meters" -> METER
                "km", "kilometer", "kilometers" -> KILOMETER
                "cm", "centimeter", "centimeters" -> CENTIMETER
                "mm", "millimeter", "millimeters" -> MILLIMETER
                "mi", "mile", "miles" -> MILE
                "yd", "yard", "yards" -> YARD
                "ft", "foot", "feet" -> FOOT
                "in", "inch", "inches" -> INCH
                "g", "gram", "grams" -> GRAM
                "kg", "kilogram", "kilograms" -> KILOGRAM
                "mg", "milligram", "milligrams" -> MILLIGRAM
                "lb", "pound", "pounds" -> POUND
                "oz", "ounce", "ounces" -> OUNCE
                "k", "kelvin", "kelvins" -> KELVIN
                "c", "dc", "celsius", "degree celsius", "degrees celsius" -> CELSIUS
                "f", "df", "fahrenheit", "degree fahrenheit", "degrees fahrenheit" -> FAHRENHEIT
                else -> NaU
            }
        }

        fun convert(input: Double, from: Unit, to: Unit): Double {
            return when (from.unitType) {
                UnitType.LENGTH, UnitType.WEIGHT -> UnitConverter.convert(input, from, to)
                UnitType.TEMPERATURE -> TemperatureConverter.convert(input, from, to)
                else -> Double.NaN
            }
        }
    }

    object TemperatureConverter {
        fun convert(value: Double, from: Unit, to: Unit): Double {
            return when (to) {
                KELVIN -> toKelvin(value, from)
                CELSIUS -> toCelsius(value, from)
                FAHRENHEIT -> toFahrenheit(value, from)
                else -> Double.NaN
            }
        }

        private fun toKelvin(input: Double, inputUnit: Unit): Double {
            return when (inputUnit) {
                CELSIUS -> input + 273.15
                FAHRENHEIT -> (input + 459.67) * 5 / 9
                KELVIN->input
                else -> Double.NaN
            }
        }

        private fun toCelsius(input: Double, inputUnit: Unit): Double {
            return when (inputUnit) {
                KELVIN -> input - 273.15
                FAHRENHEIT -> (input - 32) * 5 / 9
                CELSIUS->input
                else -> Double.NaN
            }
        }

        private fun toFahrenheit(input: Double, inputUnit: Unit): Double {
            return when (inputUnit) {
                CELSIUS -> input * 9 / 5 + 32
                KELVIN -> input * 9 / 5 - 459.67
                FAHRENHEIT->input
                else -> Double.NaN
            }
        }
    }

    object UnitConverter {
        fun convert(value: Double, from: Unit, to: Unit): Double {
            return value * from.multiplier / to.multiplier
        }
    }
}

fun main() {

    while (true) {
        print("Enter what you want to convert (or exit): ")
        val inputStg = readLine()!!.split(" ").map(String::toLowerCase)
        if (inputStg.isEmpty() || inputStg[0] == "exit") break

        //println(inputStg.joinToString(","))
        val input = inputStg[0].toDouble()

        val unit1 = when {
            inputStg[1] == "degree" || inputStg[1] == "degrees" -> {
                "${inputStg[1]} ${inputStg[2]}"
            }
            else -> {
                inputStg[1]
            }
        }

        val idxToIn = when {
            inputStg[inputStg.lastIndex - 1].contains("to") -> inputStg.lastIndex - 1
            inputStg.lastIndexOf("to") == -1 -> inputStg.lastIndexOf("in")
            else -> inputStg.lastIndexOf("to")
        }

        val unit2 = when {
            idxToIn == -1 -> {
                print("Parse error\n")
                continue
            }
            inputStg[idxToIn + 1] == "degree" || inputStg[idxToIn + 1] == "degrees" -> {
                "${inputStg[idxToIn + 1]} ${inputStg[idxToIn + 2]}"
            }
            else -> {
                inputStg[idxToIn + 1]
            }
        }
        val actualUnit1 = Unit.getByName(unit1)
        val actualUnit2 = Unit.getByName(unit2)

        print(
                if (actualUnit1.unitType == actualUnit2.unitType && (actualUnit1 != Unit.NaU || actualUnit2 != Unit.NaU)) {
                    if (input < 0.0 && (actualUnit1.unitType == UnitType.LENGTH || actualUnit1.unitType == UnitType.WEIGHT)) {
                        "${actualUnit1.unitType} shouldn't be negative.\n"
                    } else {
                        val result = Unit.convert(input, actualUnit1, actualUnit2)
                        "$input ${actualUnit1.getPrintUnit(input)} is $result ${actualUnit2.getPrintUnit(result)}\n"
                    }
                } else {
                    "Conversion from ${actualUnit1.plural} to ${actualUnit2.plural} is impossible\n"
                }
        )
    }
}

