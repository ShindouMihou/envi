package pw.mihou.envi.reflective;

import pw.mihou.envi.Envi;
import pw.mihou.envi.adapters.EnviAdapter;
import pw.mihou.envi.annotations.*;
import pw.mihou.envi.exceptions.NoSuchValidatorException;
import pw.mihou.envi.exceptions.ValidatorDisagreedException;
import pw.mihou.envi.validators.EnviValidator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

// The  Reflection Engine is written in Java since it has the best compat between Kotlin and Java.
// This leads to issues that happens on Kotlin's reflection to not happen such as Companion Objects, etc.
public class EnviReflectionEngine {

    private final EnviAdapter adapter;

    public EnviReflectionEngine(EnviAdapter adapter) {
        this.adapter = adapter;
    }

    public void into(Class<?> clazz, Stream<String> contents) {
        Map<String, String> adapted = adapter.adapt(contents);
        Arrays
                .stream(clazz.getDeclaredFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);

                        if (field.isAnnotationPresent(Skip.class)) {
                            return;
                        }

                        String name = field.getName();

                        if (field.isAnnotationPresent(Alternatively.class)) {
                            name = field.getAnnotation(Alternatively.class).name();
                        }

                        if (adapted.containsKey(name)) {

                            if (field.isAnnotationPresent(Regex.class)) {
                                Regex regex = field.getAnnotation(Regex.class);
                                if (!adapted.get(name).matches(regex.pattern())) {
                                    throw new IllegalArgumentException("The value for " + name + " does not adhere to the regex pattern provided. {value=" + adapted.get(name) + "}");
                                }
                            }

                            if (field.isAnnotationPresent(Validatable.class)) {
                                Validatable validatable = field.getAnnotation(Validatable.class);
                                EnviValidator validator = Envi.getValidators().get(validatable.with());

                                if (validator == null) {
                                    throw new NoSuchValidatorException(name, validatable.with());
                                }

                                if (!validator.resolve(adapted.get(name))) {
                                    throw new ValidatorDisagreedException(name, validatable.with(), adapted.get(name));
                                }
                            }
                           Class<?> type = field.getType();

                            if (type.equals(Boolean.class) || type.equals(boolean.class))
                                field.setBoolean(field, Boolean.parseBoolean(adapted.get(name)));
                            else if (type.equals(Integer.class) || type.equals(int.class))
                                field.setInt(field, Integer.parseInt(adapted.get(name)));
                            else if(type.equals(Long.class) || type.equals(long.class))
                                field.setLong(field, Long.parseLong(adapted.get(name)));
                            else if(type.equals(Character.class) || type.equals(char.class))
                                field.setChar(field, adapted.get(name).charAt(0));
                            else if(type.equals(String.class))
                                field.set(field, adapted.get(name));
                            else if(type.equals(Double.class) || type.equals(double.class))
                                field.set(field, Double.parseDouble(adapted.get(name)));
                            else field.set(field, adapter.resolve(adapted.get(name), type));
                        } else {
                            if (field.isAnnotationPresent(Required.class)) {
                                throw new IllegalArgumentException("The field " + name + " is annotated as a required field, but there is no value associated with the name.");
                            }

                            if (field.get(field) != null) {
                                return;
                            }

                            field.set(field, null);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
    }

}
