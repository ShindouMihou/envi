package pw.mihou.envi.collectors.system

import pw.mihou.envi.collectors.Collector

object EnvironmentFallback: Collector {
    override fun collect(key: String): String? = System.getenv(key)
}