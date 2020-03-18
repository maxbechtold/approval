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

import com.github.approval.Reporter;
import com.github.approval.utils.ExecutableExistsOnPath;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A reporter that will shell out to an executable that is presented on the user's machine to verify the test output. Note that the approval command and the difference commands can be the same.
 * <ul>
 * <li>approval command will be used for the first approval</li>
 * <li>the difference command will be used when there is already a verified file but it is not the same as the value from the user</li>
 * </ul>
 */
public class ExecutableDifferenceReporter implements Reporter {
    private final String diffCommand;
    private final String approvalCommand;
    private final String executable;

    /**
     * Main constructor for the executable reporter.
     *
     * @param approvalCommand the approval command
     * @param diffCommand     the difference command
     * @param executable the executable for which to check. If null we won't check for executable existance
     */
    public ExecutableDifferenceReporter(String approvalCommand, String diffCommand, @Nullable String executable) {
        this.approvalCommand = approvalCommand;
        this.diffCommand = diffCommand;
        this.executable = executable;
    }

    protected String getDiffCommand() {
        return diffCommand;
    }

    protected String getApprovalCommand() {
        return approvalCommand;
    }

    @Override
    public void notTheSame(byte[] oldValue, File fileForVerification, byte[] newValue, File fileForApproval) {
        try {
            int processExit = execute(buildNotTheSameCommand(fileForVerification, fileForApproval));
            if (processExit != 0) {
                throw new AssertionError(String.format("Approval failed: %s returned non-0 exit code!", approvalCommand));
            }
        } catch (IOException e) {
            throw new AssertionError(String.format("There was a problem while executing %s", approvalCommand), e);
        }
    }

    protected String[] buildNotTheSameCommand(File fileForVerification, File fileForApproval) {
        return new String[]{diffCommand, fileForApproval.getAbsolutePath(), fileForVerification.getAbsolutePath()};
    }

    @Override
    public void approveNew(byte[] value, File approvalDestination, File fileForVerification) {
        try {
            int processExit = execute(buildApproveNewCommand(approvalDestination, fileForVerification));
            if (processExit != 0) {
                throw new AssertionError(String.format("First-time approval failed: %s returned non-0 exit code!", approvalCommand));
            }

        } catch (IOException e) {
            throw new AssertionError(String.format("There was a problem while executing %s", approvalCommand), e);
        }
    }

    protected String[] buildApproveNewCommand(File approvalDestination, File fileForVerification) {
        return new String[]{approvalCommand, approvalDestination.getAbsolutePath(), fileForVerification.getAbsolutePath()};
    }

    @Override
    public boolean canApprove(File fileForApproval) {
        if (executable != null) {
            return new ExecutableExistsOnPath(executable).execute();
        }
        return true;
    }

    private int execute(String... cmdParts) throws IOException {
        final Process process = startProcess(cmdParts);
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new AssertionError("Thread was interrupted while waiting for process to finish", e);
        }
    }

    Process startProcess(String... cmdParts) throws IOException {
        return runProcess(cmdParts);
    }

    /**
     * Execute a command with the following arguments.
     *
     * @param cmdParts the command parts
     * @return the process for the command that was started
     * @throws IOException if there were any I/O errors
     */
    public static Process runProcess(String... cmdParts) throws IOException {
        List<String> commandLine = buildCommandline(cmdParts);
        Logger.getLogger(ExecutableDifferenceReporter.class.getName()).log(Level.INFO, "Running process with command line {0}", commandLine);
        return new ProcessBuilder(commandLine)
                .redirectInput(Redirect.INHERIT)
                .redirectOutput(Redirect.PIPE)
                .redirectError(Redirect.PIPE)
                .start();
    }

    static List<String> buildCommandline(String... cmdParts) {
        List<String> cmd = new ArrayList<String>();
        String[] cmdFromUser = cmdParts[0].split("\\s+");
        Collections.addAll(cmd, cmdFromUser);
        cmd.addAll(Arrays.asList(cmdParts).subList(1, cmdParts.length));
        return cmd;
    }

}
