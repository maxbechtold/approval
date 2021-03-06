package com.github.approval.pathmappers;

/*
 * #%L
 * approval
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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * User: github (Nikola Petrov) Date: 14-7-23 Time: 17:19
 */
public class ParentPathMapperTest {
    @Test
    public void shouldReturnAPathThatWasResolvedFromParent() throws Exception {
        final Path path = new ParentPathMapper(Paths.get("parent")).getPath("someValue", Paths.get("subpath"));

        Assert.assertThat(path, CoreMatchers.equalTo(Paths.get("parent", "subpath")));
    }
}
