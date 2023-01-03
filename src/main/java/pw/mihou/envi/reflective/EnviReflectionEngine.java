package pw.mihou.envi.reflective;

import org.jetbrains.annotations.Nullable;

import pw.mihou.envi.Envi;
import pw.mihou.envi.adapters.EnviAdapter;
import pw.mihou.envi.annotations.*;
import pw.mihou.envi.collectors.Collector;
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
    public @Nullable Collector collector;

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

                        // Kotlin's INSTANCE field.
                        if (field.getClass() == clazz && field.getName().equals("INSTANCE")) {
                            return;
                        }

                        String name = field.getName();

                        if (field.isAnnotationPresent(Alternatively.class)) {
                            name = field.getAnnotation(Alternatively.class).name();
                        }

                        String value = adapted.get(name);
                        if (value == null && collector != null) {
                            value = collector.collect(name);
                        }

                        if (value != null) {
                            if (field.isAnnotationPresent(Regex.class)) {
                                Regex regex = field.getAnnotation(Regex.class);
                                if (!value.matches(regex.pattern())) {
                                    throw new IllegalArgumentException("The value for " + name + " does not adhere to the regex pattern provided. {value=" + value + "}");
                                }
                            }

                            if (field.isAnnotationPresent(Validatable.class)) {
                                Validatable validatable = field.getAnnotation(Validatable.class);
                                EnviValidator validator = Envi.getValidators().get(validatable.with());

                                if (validator == null) {
                                    throw new NoSuchValidatorException(name, validatable.with());
                                }

                                if (!validator.resolve(value)) {
                                    throw new ValidatorDisagreedException(name, validatable.with(), value);
                                }
                            }
                           Class<?> type = field.getType();

                            if (type.equals(Boolean.class) || type.equals(boolean.class))
                                field.setBoolean(field, Boolean.parseBoolean(value));
                            else if (type.equals(Integer.class) || type.equals(int.class))
                                field.setInt(field, Integer.parseInt(value));
                            else if(type.equals(Long.class) || type.equals(long.class))
                                field.setLong(field, Long.parseLong(value));
                            else if(type.equals(Character.class) || type.equals(char.class))
                                field.setChar(field, value.charAt(0));
                            else if(type.equals(String.class))
                                field.set(field, value);
                            else if(type.equals(Double.class) || type.equals(double.class))
                                field.set(field, Double.parseDouble(value));
                            else field.set(field, adapter.resolve(value, type));
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
