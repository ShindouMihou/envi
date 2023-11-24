package pw.mihou.envi.collectors

fun interface Collector {
    fun collect(key: String): String?
}