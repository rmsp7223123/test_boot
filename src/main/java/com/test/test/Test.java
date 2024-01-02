package com.test.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {
	public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(DataSourceProperties.class);
        DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
        dataSourceProperties.printUsernameAndPassword();
    }

}
