package com.github.approval.example;

import com.github.approval.Approval;
import com.github.approval.reporters.Reporters;
import org.junit.Test;

import java.nio.file.Paths;

public class EntityConverterExample {
    @Test
    public void customEntityTest() {
        Entity entity = new Entity("Nikola", 30);
        Approval<Entity> approver = Approval.of(Entity.class)
                .withReporter(Reporters.console())
                .withConverter(new EntityConverter())
                .build();
        approver.verify(entity, Paths.get("src/test/resources/approval/example/entity.verified"));
    }
}
