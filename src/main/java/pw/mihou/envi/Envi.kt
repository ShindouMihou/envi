package pw.mihou.envi

import pw.mihou.envi.adapters.EnviAdapter
import pw.mihou.envi.collectors.Collector
import pw.mihou.envi.reflective.EnviReflectionEngine
import pw.mihou.envi.validators.EnviValidator
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.stream.Stream

class Envi internal constructor(adapter: EnviAdapter) {

    private val engine = EnviReflectionEngine(adapter)

    companion object {
        @JvmStatic
        val validators = mutableMapOf<String, EnviValidator>()

        @JvmStatic
        fun createConfigurator(adapter: EnviAdapter) = Envi(adapter)
    }

    fun fallback(collector: Collector?): Envi {
        engine.collector = collector
        return this
    }

    fun read(file: File, into: Class<*>) {
        if (!file.exists()) {
            if (engine.collector == null) {
                throw IOException("The file (${file.absolutePath}) does not exist, and there is no fallback collector configured.")
            }

            engine.into(into, Stream.empty())
            return
        }

        engine.into(into, Files.lines(file.toPath()))
    }

    fun read(contents: String, into: Class<*>) {
        engine.into(into, Stream.of(contents))
    }

    fun read(stream: Stream<String>, into: Class<*>) {
        engine.into(into, stream)
    }

}