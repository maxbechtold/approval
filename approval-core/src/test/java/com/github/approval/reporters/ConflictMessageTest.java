package com.github.approval.reporters;

/*
 * #%L
 * approval
 * %%
 * Copyright (C) 2020 Max Bechtold
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

import org.junit.Test;

public class ConflictMessageTest {

    @Test
    public void buildsConflictMessage() {
        String result = new ConflictMessage("old", "new").create();
        assertThat(result).isEqualTo(String.format("<<<<<<< expected%nold%n=======%nnew%n>>>>>>> actual"));
    }

    @Test
    public void addsNoExtraNewLine() {
        String _n = System.lineSeparator();
        String oldValue = "old" + _n;
        String newValue = "new"+ _n;
        String result = new ConflictMessage(oldValue, newValue).create();
        assertThat(result).isEqualTo(String.format("<<<<<<< expected%n%s=======%n%s>>>>>>> actual", oldValue, newValue));
    }

    @Test
    public void addsNoExtraNewLineForEmptyValue() {
        String oldValue = "";
        String newValue = "";
        String result = new ConflictMessage(oldValue, newValue).create();
        assertThat(result).isEqualTo(String.format("<<<<<<< expected%n%s=======%n%s>>>>>>> actual", oldValue, newValue));
    }
    
    
}
