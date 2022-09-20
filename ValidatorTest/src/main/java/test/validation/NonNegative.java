package test.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = NonNegativeValidator.class)
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface NonNegative {

    String message() default "No Negative value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
