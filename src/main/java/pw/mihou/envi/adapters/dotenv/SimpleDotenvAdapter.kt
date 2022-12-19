package pw.mihou.envi.adapters.dotenv

import pw.mihou.envi.adapters.EnviAdapter
import java.util.stream.Stream

object SimpleDotenvAdapter: EnviAdapter {
    override fun adapt(stream: Stream<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        stream.forEach { line ->
            if (!line.contains("=") || line.startsWith("#")) return@forEach
            val keyValue = line.split("=", limit = 2)

            if (keyValue.size < 2 || keyValue[1].isEmpty()) {
                return@forEach
            }

            map[keyValue[0]] = keyValue[1]
        }
        return map
    }
}