/**
 *  Copyright 2012-2017 Gunnar Morling (http://www.gunnarmorling.de/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
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
package org.mapstruct.ap.internal.model;

import org.mapstruct.ap.internal.model.MethodReference;
import org.mapstruct.ap.internal.model.common.Type;

public class BuilderMappingModel {

    private final Type finalType;
    private final Type builderType;
    private final MethodReference builderCreationMethod;
    private final MethodReference finalizeMethod;
    private final MethodReference thawMethod;

    public BuilderMappingModel(Type finalType, Type builderType, MethodReference builderCreationMethod,
        MethodReference finalizeMethod, MethodReference thawMethod) {

        this.finalType = finalType;
        this.builderType = builderType;
        this.builderCreationMethod = builderCreationMethod;
        this.finalizeMethod = finalizeMethod;
        this.thawMethod = thawMethod;
    }

    public Type getFinalType() {
        return finalType;
    }

    public Type getBuilderType() {
        return builderType;
    }

    public MethodReference getBuilderCreationMethod() {
        return builderCreationMethod;
    }

    public MethodReference getBuildMethod() {
        return finalizeMethod;
    }

    public MethodReference getThawMethod() {
        return thawMethod;
    }
}
