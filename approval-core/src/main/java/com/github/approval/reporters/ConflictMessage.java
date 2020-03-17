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

/**
 * Builds a GIT style conflict message to help identify approval problems without IDE support. 
 * 
 * @author Max Bechtold
 *
 */
public class ConflictMessage {

    private static final String NEWLINE = System.lineSeparator();
    private static final String CONFLICT_TEMPLATE = "<<<<<<< expected%n%s=======%n%s>>>>>>> actual";
    
    private final String oldValue;
    private final String newValue;

    public ConflictMessage(String oldValue, String newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String create() {
        String conflictMessage = String.format(CONFLICT_TEMPLATE, getParam(oldValue), getParam(newValue));
        return conflictMessage;
    }

    private String getParam(String value) {
        if (value.isEmpty() || value.endsWith(System.lineSeparator())) {
            return value;
        }
        return value + NEWLINE;
    }

}
