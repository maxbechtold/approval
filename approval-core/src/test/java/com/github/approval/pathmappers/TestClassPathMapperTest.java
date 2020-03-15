package com.github.approval.pathmappers;

/*
 * #%L
 * approval
 * %%
 * Copyright (C) 2018 Max Bechtold
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class TestClassPathMapperTest {

    private Class<?> testClass;

    @Before
    public void setUp() {
        testClass = getClass();
    }

    @Test
    public void mapsPackageStructureToPath() throws Exception {
        String approvalId = "test-id";
        Path tempDir = new File("root").toPath();
        TestClassPathMapper<Object> mapper = new TestClassPathMapper<>(testClass, tempDir, approvalId);

        Path approvalFilePath = new File("file").toPath();
        Path approvalPath = mapper.getPath(new Object(), approvalFilePath);

        String separator = File.separator;
        String classPath = getClass().getName().replace('.', File.separatorChar);

        assertThat(approvalPath.toString()) //
                .isEqualTo(tempDir.toString() + separator //
                        + classPath + separator //
                        + approvalId + separator //
                        + approvalFilePath.toString());
    }

}
