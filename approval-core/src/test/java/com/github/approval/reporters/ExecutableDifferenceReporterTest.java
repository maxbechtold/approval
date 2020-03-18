package com.github.approval.reporters;

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

import com.github.approval.TestTempFile;
import com.github.approval.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.approval.TestUtils.RAW_VALUE;
import static com.github.approval.TestUtils.forApproval;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by github on 2/2/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecutableDifferenceReporterTest {

    public static final int ERROR_EXIT_CODE = 2;
    public static final int OK_CODE = 0;
    public static final String GVIM_EXECUTABLE = "gvim -f";
    public static final String GDIFFVIM_EXECUTABLE = "gvimdiff -f";
    @Rule
    public TestTempFile testFile = new TestTempFile();
    private ExecutableDifferenceReporter executableDifferenceReporter;

    @Before
    public void setupMocks() {
        executableDifferenceReporter = Mockito.spy(new ExecutableDifferenceReporter(GVIM_EXECUTABLE, GDIFFVIM_EXECUTABLE, "gvimdiff"));
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAssertionError_IfThereIsErrorWhileExecuting() throws Exception {
        doThrow(new IOException("error in exec")).when(executableDifferenceReporter).startProcess(Mockito.<String>anyVararg());

        executableDifferenceReporter.approveNew(RAW_VALUE, forApproval(testFile), testFile.file());
    }

    @Test
    public void shouldThrowAnExceptionIfExecutableReturnedErrorCode() throws Exception {
        for(int exitCode : new int[] {ERROR_EXIT_CODE, -ERROR_EXIT_CODE}) {
            Process process = Mockito.mock(Process.class);
            when(process.waitFor()).thenReturn(exitCode);
            doReturn(process).when(executableDifferenceReporter).startProcess(GVIM_EXECUTABLE, forApproval(testFile).getAbsolutePath(), testFile.file().getAbsolutePath());
            try {
                executableDifferenceReporter.approveNew("test content".getBytes(StandardCharsets.UTF_8), forApproval(testFile), testFile.file());
                Assert.fail("Should throw an exception");
            } catch (AssertionError error) {
                assertTrue(true);
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAssertionErrorOnInterruptionAlthoughThatShouldntHappen() throws Exception {
        //assign
        Process process = Mockito.mock(Process.class);
        when(process.waitFor()).thenThrow(new InterruptedException("test exception"));

        doReturn(process).when(executableDifferenceReporter).startProcess(GVIM_EXECUTABLE, forApproval(testFile).getAbsolutePath(), testFile.file().getAbsolutePath());

        //act
        executableDifferenceReporter.approveNew(RAW_VALUE, forApproval(testFile), testFile.file());
    }

    @Test
    public void shouldNotThrowIfReporterExecutableExecutedExitedWithNonErrorValue() throws Exception {
        Process process = Mockito.mock(Process.class);
        when(process.waitFor()).thenReturn(OK_CODE);
        doReturn(process).when(executableDifferenceReporter).startProcess(GVIM_EXECUTABLE, forApproval(testFile).getAbsolutePath(), testFile.file().getAbsolutePath());

        executableDifferenceReporter.approveNew("test content".getBytes(StandardCharsets.UTF_8), forApproval(testFile), testFile.file());
    }

    @Test(expected = AssertionError.class)
    public void shouldThrowAssertionError_IfThereIsErrorWhileExecutingNotSame() throws Exception {
        doThrow(new IOException("error in exec")).when(executableDifferenceReporter).startProcess(Mockito.<String>anyVararg());
        executableDifferenceReporter.notTheSame(RAW_VALUE, testFile.file(), (TestUtils.VALUE + " difference ").getBytes(StandardCharsets.UTF_8), forApproval(testFile));
    }

    @Test
    public void shouldBuildTheCommandLineProperlyIfInitialCommandsHaveArguments() throws Exception {
        List<String> cmd = ExecutableDifferenceReporter.buildCommandline(GVIM_EXECUTABLE, "test");
        assertThat(cmd.size(), CoreMatchers.equalTo(3));
        assertThat(cmd.get(1), CoreMatchers.equalTo("-f"));
    }

    @Test(expected = AssertionError.class)
    public void notTheSameShouldPropagateErrorCodesFromDiffTool() throws Exception {
        Process process = Mockito.mock(Process.class);
        when(process.waitFor()).thenReturn(ERROR_EXIT_CODE);
        doReturn(process).when(executableDifferenceReporter).startProcess(Mockito.<String>anyVararg());
        byte[] changedValue = (TestUtils.VALUE + "!diff").getBytes(StandardCharsets.UTF_8);
        
        executableDifferenceReporter.notTheSame(RAW_VALUE, testFile.file(), changedValue, forApproval(testFile));
    }

    @Test(expected = IOException.class)
    public void shouldThrowAnExceptionIfExecutableIsNotFound() throws Exception {
        Process process = executableDifferenceReporter.startProcess("unexistingcommand", "-invalid-flag");
        process.waitFor();
    }

    @Test
    public void shouldReportProperDiffCommandAndApprovalCommandsWhenAsked() throws Exception {
        final String approvalCommand = executableDifferenceReporter.getApprovalCommand();
        assertThat(approvalCommand, CoreMatchers.equalTo(GVIM_EXECUTABLE));

        final String diffCommand = executableDifferenceReporter.getDiffCommand();
        assertThat(diffCommand, CoreMatchers.equalTo(GDIFFVIM_EXECUTABLE));
    }
    
    @Test
    public void shouldNotCheckIfExecutableExistsIfItIsNull() throws Exception {
        final boolean canApprove = new ExecutableDifferenceReporter(GVIM_EXECUTABLE, GDIFFVIM_EXECUTABLE, null).canApprove(testFile.file());
        assertTrue(canApprove);
    }
}
