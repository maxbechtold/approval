package com.github.approval.example.converters;

import com.github.approval.Approval;
import com.github.approval.reporters.Reporters;
import org.junit.Test;

import java.nio.file.Paths;

public class PersonApprovalTest {
    private static final Approval<Person> APPROVER = Approval.of(Person.class)
            .withConverter(new JacksonConverter<Person>())
            .withReporter(Reporters.gvim())
            .build();

    @Test
    public void serializesToJSON() throws Exception {
        final Person person = new Person("Luther Blissett", "lb@example.com");
        APPROVER.verify(person, Paths.get("src/test/resources/person-approval.json"));
    }

}
