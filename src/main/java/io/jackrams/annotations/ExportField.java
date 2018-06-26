package io.jackrams.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportField {
  String title() default "";
  String value() default"";
  String formate() default "";
  TypeEnum type() default TypeEnum.Null;
  ViewType viewType() default ViewType.String;


}
