package ir.cherikcoders.springautoui.util.annotaions;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IncludeInUI {
    boolean showInMenu() default true;
    String menuTitle() default "";
}
