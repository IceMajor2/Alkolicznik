package com.demo.alkolicznik.api;

import com.demo.alkolicznik.config.DisabledVaadinContext;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.TestClassOrder;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "enable.image.database=true")
@Import(DisabledVaadinContext.class)
@ActiveProfiles({ "main", "image" })
@TestClassOrder(ClassOrderer.Random.class)
class StoreImageTest {


}