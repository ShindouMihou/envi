package pw.mihou.envi.validators

fun interface EnviValidator {
    fun resolve(contents: String): Boolean
}