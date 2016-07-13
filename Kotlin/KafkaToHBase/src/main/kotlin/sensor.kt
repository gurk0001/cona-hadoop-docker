data class SensorEvent(val id: String, val timestamp: String, val state: Boolean)

fun parseRawSensorData(raw: String): SensorEvent {
    val split = raw.split(',')
    val event = SensorEvent(split[0], split[1], when (split[2]) {
        "0" -> false
        "1" -> true
        else -> throw IllegalArgumentException("${split[2]} in $raw is not true (1) or false (0)")
    })
    return event
}
