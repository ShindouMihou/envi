package pw.mihou.envi.exceptions

class NoSuchValidatorException(field: String, validator: String):
    RuntimeException("No validators with the name $validator has been registered with Envi. {field=$field}")