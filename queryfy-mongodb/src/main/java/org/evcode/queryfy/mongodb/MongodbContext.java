/*
 *  Copyright 2018 EVCode
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.evcode.queryfy.mongodb;

import org.evcode.queryfy.mongodb.converter.DateTimeConverter;
import org.evcode.queryfy.mongodb.converter.TypeConverter;

import java.util.*;

public class MongodbContext {

    private final Map<String, String> queryPaths;
    private final Map<String, String> projectionPaths;
    private Set<TypeConverter> typeConverters = createDefaultTypeConverters();

    protected MongodbContext(Map<String, String> queryPaths, Map<String, String> projectionPaths) {
        this.queryPaths = queryPaths;
        this.projectionPaths = projectionPaths;
    }

    protected MongodbContext(Map<String, String> queryPaths, Map<String, String> projectionPaths,
                             Set<TypeConverter> typeConverters) {
        this.queryPaths = queryPaths;
        this.projectionPaths = projectionPaths;
        this.typeConverters = typeConverters != null ? typeConverters : Collections.emptySet();
    }

    private static Set<TypeConverter> createDefaultTypeConverters() {
        return new HashSet<>(Arrays.asList(new DateTimeConverter()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> getQueryPaths() {
        return Collections.unmodifiableMap(queryPaths);
    }

    public Map<String, String> getProjectionPaths() {
        return Collections.unmodifiableMap(projectionPaths);
    }

    public Set<TypeConverter> getTypeConverters() {
        return Collections.unmodifiableSet(typeConverters);
    }

    public String resolveProjectionPath(String path) {
        String resolvedPath = projectionPaths.get(path);
        if (resolvedPath == null) {
            throw new IllegalArgumentException("Projection path " + path + " not found");
        }
        return resolvedPath;
    }

    public String resolveQueryPath(String path) {
        String resolvedPath = queryPaths.get(path);
        if (resolvedPath == null) {
            throw new IllegalArgumentException("Query path " + path + " not found");
        }
        return resolvedPath;
    }

    public static class Builder {

        private final Set<TypeConverter> typeConverters = createDefaultTypeConverters();
        private final Map<String, String> queryPaths = new HashMap<>();
        private final Map<String, String> projectionPaths = new HashMap<>();

        public Builder() {
        }

        public Builder withPath(String name) {
            return withPath(name, name);
        }

        public Builder withPath(String name, String path) {
            return withQueryPath(name, path).withProjectionPath(name, path);
        }

        public Builder withQueryPath(String name) {
            return withQueryPath(name, name);
        }

        public Builder withQueryPath(String name, String path) {
            queryPaths.put(name, path);
            return this;
        }

        public Builder withProjectionPath(String name) {
            return withProjectionPath(name, name);
        }

        public Builder withTypeConverter(TypeConverter converter) {
            typeConverters.add(converter);
            return this;
        }

        public Builder withNoTypeConverters() {
            typeConverters.clear();
            return this;
        }

        public Builder withProjectionPath(String name, String path) {
            projectionPaths.put(name, path);
            return this;
        }

        public MongodbContext build() {
            return new MongodbContext(queryPaths, projectionPaths, typeConverters);
        }
    }
}