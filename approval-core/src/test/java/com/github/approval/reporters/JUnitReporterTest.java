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

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.assertj.core.util.Files;
import org.junit.Test;
import org.opentest4j.TestAbortedException;

import com.github.approval.utils.ApprovalScriptWriter;
import com.google.common.base.Charsets;

public class JUnitReporterTest {

    @Test
    public void approveNewThrowsError() {
        JUnitReporter reporter = new JUnitReporter(ApprovalScriptWriter.create("script"));
        try {
            reporter.approveNew("new".getBytes(Charsets.UTF_8), tmpFile(), tmpFile());
            fail("Reporter did not throw");
        } catch (Error e) {
            assertThat(e).isInstanceOf(AssertionError.class);
            assertThat(e.getCause()).isInstanceOf(AssertionError.class);
        }
    }
    
    @Test
    public void notTheSameThrowsError() {
        JUnitReporter reporter = new JUnitReporter(ApprovalScriptWriter.create("script"));
        try {
            reporter.notTheSame("old".getBytes(Charsets.UTF_8), tmpFile(), "new".getBytes(Charsets.UTF_8), tmpFile());
            fail("Reporter did not throw");
        } catch (Error e) {
            assertThat(e).isInstanceOf(AssertionError.class);
            assertThat(e.getCause()).isInstanceOf(AssertionError.class);
        }
    }
    
    @Test
    public void notTheSameAbortsWithAcceptableDeviation() {
        JUnitReporter reporter = new JUnitReporter(ApprovalScriptWriter.create("script"), 0.5);
        try {
            reporter.notTheSame("old".getBytes(Charsets.UTF_8), tmpFile(), "o1d".getBytes(Charsets.UTF_8),tmpFile());
            fail("Reporter did not throw");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(TestAbortedException.class);
        }
    }

    private File tmpFile() {
        return Files.newTemporaryFile();
    }
}
