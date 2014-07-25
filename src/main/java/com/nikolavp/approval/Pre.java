package com.nikolavp.approval;

/*
 * #%L
 * com.nikolavp.approval:core
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

import javax.annotation.Nullable;

/**
 * Pre conditions exceptions.
 */
public final class Pre {
    private Pre() {

    }

    /**
     * Verify that a value is not null.
     *
     * @param value the value to verify
     * @param name the name of the value that will be used in the exception message.
     */
    public static void notNull(@Nullable Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null!");
        }
    }
}
