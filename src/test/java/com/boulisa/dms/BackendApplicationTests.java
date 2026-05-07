package com.boulisa.dms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class BackendApplicationTests {

    ApplicationModules modules = ApplicationModules.of(BackendApplication.class);

    @Test
    void contextLoads() {
    }

    @Test
    void verifyModularity() {
        modules.verify();
    }

    @Test
    void writeDocumentationSnippets() {
        modules.forEach(System.out::println);
        new Documenter(modules).writeModulesAsPlantUml();
    }

}
