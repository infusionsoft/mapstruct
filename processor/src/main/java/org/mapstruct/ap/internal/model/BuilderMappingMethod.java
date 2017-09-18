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

import static org.mapstruct.ap.internal.util.Collections.emptySet;

import org.mapstruct.ap.internal.model.common.Assignment;
import org.mapstruct.ap.internal.model.common.ParameterBinding;
import org.mapstruct.ap.internal.model.common.SourceRHS;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.source.Method;

/**
 * A {@link MappingMethod} that uses a simple assignment to convert from one known type to another.  It's
 * assumed that any source input does not require any additional mapping.
 *
 * @author Eric Martineau
 */
public class BuilderMappingMethod extends MappingMethod {

    private final Assignment builderMapping;
    private final Assignment buildMethod;
    private final Type builderType;
    private final Type finalType;

    private BuilderMappingMethod(Method method, Assignment builderMapping, Assignment buildMethod,
        BuilderMappingModel builderModel) {

        super( method );
        this.builderMapping = builderMapping;
        this.buildMethod = buildMethod;
        this.builderType = builderModel.getBuilderType();
        this.finalType = builderModel.getFinalType();
    }

    public Assignment getBuilderMapping() {
        return builderMapping;
    }

    public Assignment getBuildMethod() {
        return buildMethod;
    }

    public Type getBuilderType() {
        return builderType;
    }

    public Type getFinalType() {
        return finalType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Method declaredMethod;
        private BuilderMappingModel builderModel;
        private Method builderMappingMethod;

        public Builder builderMappingMethod(Method builderMappingMethod) {
            this.builderMappingMethod = builderMappingMethod;
            return this;
        }

        public Builder declaredMethod(Method declaredMethod) {
            this.declaredMethod = declaredMethod;
            return this;
        }

        public Builder builderModel(BuilderMappingModel builderMappingModel) {
            this.builderModel = builderMappingModel;
            return this;
        }

        public BuilderMappingMethod build() {
            final Type builderType = builderModel.getBuilderType();

            //
            // Step 1. Invoke the forged method that maps source parameters to the intermediate builder.  Store result
            // in {@code mappedBuilder}
            //
            final MethodReference builderMapperReference = MethodReference.forLocalMethod( builderMappingMethod,
                ParameterBinding.fromParameters( builderMappingMethod.getParameters() ) );

            //
            // Step 2. Invoke the build method on {@code mappedBuilder}
            //
            final MethodReference buildMethod = builderModel.getBuildMethod();
            buildMethod.setAssignment( new SourceRHS( "mappedBuilder", builderType, emptySet(),
                "mapped builder") );
            buildMethod.setSourceLocalVarName( "mappedBuilder" );

            return new BuilderMappingMethod( declaredMethod,  builderMapperReference, buildMethod, builderModel );
        }
    }
}

