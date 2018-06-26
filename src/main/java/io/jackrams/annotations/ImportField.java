package io.jackrams.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportField {
  String title() default "";
  String value() default"";
  String format() default "";
  String titles() default "";
  ViewType viewType() default ViewType.String;

}
