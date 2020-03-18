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

import java.io.File;
import java.nio.file.Path;

import com.github.approval.PathMapper;

/**
 * Maps test classes to a path matching their package structure, with the final directory matching the class name.
 * 
 * @param <T> arbitrary
 * 
 * @author Max Bechtold
 *
 */
// TODO maxbechtold Very similar to ParentPathMapper, could be merged
public class TestClassPathMapper<T> implements PathMapper<T> {

    private final Path approvalPath;

    public TestClassPathMapper(Class<?> testClass, Path basePath, String approvalId) {
        approvalPath = basePath.resolve(testClass.getName().replace(".", File.separator)).resolve(approvalId);
    }

    @Override
    public Path getPath(Object value, Path approvalFilePath) {
        return approvalPath.resolve(approvalFilePath);
    }

}
