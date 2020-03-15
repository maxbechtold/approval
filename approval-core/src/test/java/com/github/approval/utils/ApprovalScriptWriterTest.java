package com.github.approval.utils;

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

import org.junit.Test;

import com.github.approval.utils.ApprovalScriptWriter;

public class ApprovalScriptWriterTest {

    private File sourceFile = new File("source");
    private File targetFile = new File("target");

    @Test
    public void writesSingleMoveCommand() throws Exception {
        File scriptFile = File.createTempFile("script", ".bat");
        ApprovalScriptWriter approvalScriptWriter = new ApprovalScriptWriter("move", "/Y", scriptFile);

        approvalScriptWriter.addMoveCommand(sourceFile, targetFile);
        approvalScriptWriter.updateScript();

        assertThat(scriptFile.exists()).isTrue();
        assertThat(scriptFile).hasContent(
                "move /Y \"" + sourceFile.getAbsolutePath() + "\" \"" + targetFile.getAbsolutePath() + "\"");
    }

    @Test
    public void writesSeveralMoveCommands() throws Exception {
        File scriptFile = File.createTempFile("script", ".bat");
        ApprovalScriptWriter approvalScriptWriter = new ApprovalScriptWriter("move", "/Y", scriptFile);

        approvalScriptWriter.addMoveCommand(sourceFile, targetFile);
        approvalScriptWriter.addMoveCommand(targetFile, sourceFile);
        approvalScriptWriter.updateScript();

        assertThat(scriptFile.exists()).isTrue();
        assertThat(scriptFile)
                .hasContent("move /Y \"" + sourceFile.getAbsolutePath() + "\" \"" + targetFile.getAbsolutePath() + "\"" //
                        + System.lineSeparator() //
                        + "move /Y \"" + targetFile.getAbsolutePath() + "\" \"" + sourceFile.getAbsolutePath() + "\"");
    }

    @Test
    public void writesLinuxCommands() throws Exception {
        File scriptFile = File.createTempFile("script", "");
        ApprovalScriptWriter approvalScriptWriter = new ApprovalScriptWriter("mv", "-f", scriptFile);

        approvalScriptWriter.addMoveCommand(sourceFile, targetFile);
        approvalScriptWriter.updateScript();

        assertThat(scriptFile.exists()).isTrue();
        assertThat(scriptFile)
                .hasContent("mv -f \"" + sourceFile.getAbsolutePath() + "\" \"" + targetFile.getAbsolutePath() + "\"");
    }

    @Test
    public void deletesScriptIfNoCommandsGiven() throws Exception {
        File scriptFile = File.createTempFile("script", "");
        assertThat(scriptFile.exists()).isTrue();

        ApprovalScriptWriter approvalScriptWriter = new ApprovalScriptWriter("move", "/Y", scriptFile);
        approvalScriptWriter.updateScript();

        assertThat(scriptFile.exists()).isFalse();
    }
}
