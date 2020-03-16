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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Allows to create a platform dependent script file which helps in creating or replacing .approval files.
 * 
 * @author Max Bechtold
 *
 */
public class ApprovalScriptWriter {

    /**
     * .
     */
    private static final class Factory extends CrossPlatformCommand<ApprovalScriptWriter> {

        private final String scriptName;

        private Factory(String scriptName) {
            this.scriptName = scriptName;
        }
        
        @Override
        protected ApprovalScriptWriter onWindows() {
            return new ApprovalScriptWriter("move", "/Y", getScriptFile(".bat"));
        }

        @Override
        protected ApprovalScriptWriter onUnix() {
            return new ApprovalScriptWriter("mv", "-f", getScriptFile(""));
        }

        private File getScriptFile(String ext) {
            return new File(scriptName + ext);
        }
    }
        
    private final File scriptFile;
    private final String moveCommand;
    private final String overwriteFlag;

    private StringBuilder scriptBuilder;

    public ApprovalScriptWriter(String moveCommand, String overwriteFlag, File scriptFile) {
        this.overwriteFlag = overwriteFlag;
        this.moveCommand = moveCommand;
        this.scriptFile = scriptFile;
        scriptBuilder = new StringBuilder();
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public void addMoveCommand(File sourceFile, File targetFile) {
        scriptBuilder.append(moveCommand);
        scriptBuilder.append(" ");
        scriptBuilder.append(overwriteFlag);
        scriptBuilder.append(" ");
        scriptBuilder.append(quote(sourceFile));
        scriptBuilder.append(" ");
        scriptBuilder.append(quote(targetFile));
        scriptBuilder.append(System.lineSeparator());
    }

    private String quote(File file) {
        return "\"" + file.getAbsolutePath() + "\"";
    }

    public boolean updateScript() {
        if (scriptBuilder.length() == 0) {
            tryToDeleteScript();
            return false;
        }

        writeScriptContent();
        return true;
    }

    private void tryToDeleteScript() {
        try {
            Files.delete(scriptFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete existing approval script", e);
        }
    }

    private void writeScriptContent() {
        try {
            Files.write(scriptFile.toPath(), scriptBuilder.toString().getBytes(StandardCharsets.UTF_8));
//            scriptFile.setExecutable(true, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApprovalScriptWriter create(String approvalScriptName) {
        return new ApprovalScriptWriter.Factory(approvalScriptName).execute();
    }

}
