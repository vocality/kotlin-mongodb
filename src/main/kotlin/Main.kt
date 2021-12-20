package fr.vocality.tutorials

fun callBackIfTrue(condition: Boolean, cb: () -> Unit) {
    if (condition) cb()
}

fun main(args: Array<String>) {
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    callBackIfTrue(false) {
        println("Condition was true !")
    }

    val coordinates = listOf<Double>(1.5, 45.879)
    val (longitude, latitude) = coordinates

    println("latitude: $latitude / longitude: $longitude")
}
