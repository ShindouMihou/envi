package pw.mihou.envi

import pw.mihou.envi.adapters.EnviAdapter
import pw.mihou.envi.reflective.EnviReflectionEngine
import pw.mihou.envi.validators.EnviValidator
import java.io.File
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

    fun read(file: File, into: Class<*>) {
        engine.into(into, Files.lines(file.toPath()))
    }

    fun read(contents: String, into: Class<*>) {
        engine.into(into, Stream.of(contents))
    }

}