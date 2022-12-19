package pw.mihou.envi.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Regex(val pattern: String)
