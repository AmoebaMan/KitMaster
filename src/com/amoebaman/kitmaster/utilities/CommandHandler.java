package com.amoebaman.kitmaster.utilities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {

	String name();
	String[] aliases() default { "" };
	String description() default "";
	String usage() default "";
	String permission() default "";
	String permissionMessage() default "";
	
}