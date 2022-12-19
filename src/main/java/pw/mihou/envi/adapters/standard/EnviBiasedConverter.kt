package pw.mihou.envi.adapters.standard

object EnviBiasedConverter {

    val adapters = mutableMapOf<Class<*>, EnviFieldAdapter<*>>()

    @Suppress("UNCHECKED_CAST")
    fun <Type> adapt(contents: String, clazz: Class<Type>): Type =
        adapters[clazz]?.adapt(contents) as Type ?:
        throw NoSuchElementException("No adapter has been found for the following contents: $contents")

}

fun interface EnviFieldAdapter<Type> {
    fun adapt(contents: String): Type
}