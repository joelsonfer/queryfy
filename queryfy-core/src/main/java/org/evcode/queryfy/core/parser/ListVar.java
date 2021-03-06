/*
 * Copyright 2017 EVCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evcode.queryfy.core.parser;

import org.parboiled.support.Var;

import java.util.LinkedList;

class ListVar<T> extends Var<LinkedList<T>> {

    private LinkedList<T> values = new LinkedList<>();
    private Class valueType;
    private boolean allowMultipleTypes = false;

    public ListVar() {
    }

    public ListVar(boolean allowMultipleTypes) {
        this.allowMultipleTypes = allowMultipleTypes;
    }

    public boolean add(T value) {
        if (isNotSet()) {
            set(values);
        }
        if (!allowMultipleTypes && valueType != null && !valueType.equals(value.getClass())) {
            throw new IllegalArgumentException("Illegal value type. An instance of " + valueType.getName() + " was expected");
        }

        this.valueType = value.getClass();
        this.values.add(value);
        return true;
    }
}
