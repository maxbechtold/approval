package com.github.approval.sesame;

/*
 * #%L
 * approval-sesame
 * %%
 * Copyright (C) 2014 Nikolavp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.approval.Approval;
import com.github.approval.Approvals;
import org.junit.Test;
import org.openrdf.model.Graph;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.nio.file.Paths;

public class GraphReporterIT {
    @Test
    public void shouldProperlyConvertTheDotFormatAndThenReportItInConfigredApplication() throws Exception {
        ValueFactory v = new ValueFactoryImpl();

        Graph graph = new TreeModel();
        graph.add(v.createStatement(
                v.createURI("http://test.urn"),
                v.createURI("http://predicate"),
                v.createLiteral("Test label")
        ));

        graph.add(v.createStatement(
                v.createURI("http://test.urn1"),
                v.createURI("http://predicate1"),
                v.createLiteral("Test label1")
        ));

        Approval<Graph> approval = Approval.of(Graph.class)
                .withConverter(new GraphConverter())
                .withReporter(GraphReporter.getInstance())
                .build();
        approval.verify(graph, Paths.get("src/test/resources/approvals/shouldProperlyConvertTheDotFormatAndThenReportItInConfigredApplication.dot"));
    }
}
