package pw.mihou.envi.exceptions

class ValidatorDisagreedException(field: String, validator: String, value: String):
    RuntimeException("The validator with the name $validator has disagreed with the value of the field $field. {value=$value}")