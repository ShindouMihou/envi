package pw.mihou.envi.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Alternatively(val name: String)
