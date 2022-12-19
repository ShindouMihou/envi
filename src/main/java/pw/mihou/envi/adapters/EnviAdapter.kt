package pw.mihou.envi.adapters

import pw.mihou.envi.adapters.standard.EnviBiasedConverter
import java.util.stream.Stream

interface EnviAdapter {

    fun adapt(stream: Stream<String>): Map<String, String>
    fun <Type> resolve(contents: String, clazz: Class<Type>): Type = EnviBiasedConverter.adapt(contents, clazz)

}