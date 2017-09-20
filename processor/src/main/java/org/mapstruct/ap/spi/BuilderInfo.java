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
package org.mapstruct.ap.spi;

import javax.lang.model.type.TypeMirror;

/**
 * Information about how to construct immutable instances using a builder.  Generated by
 * {@link org.mapstruct.ap.spi.BuilderProvider}.  During processing, it's converted into
 * {@link BuilderMappingModel}
 */
public class BuilderInfo {

    private final TypeMirror builderType;
    private final TypeMirror resultType;
    private final String builderMethodName;
    private final String buildMethodName;
    private final String thawMethodName;

    private BuilderInfo(TypeMirror resultType, TypeMirror builderType,
        String builderMethodName, String buildMethodName, String thawMethodName ) {

        this.thawMethodName = thawMethodName;
        assert resultType != null : "Target type must not be null";
        assert builderType != null : "Builder type must not be null";
        assert buildMethodName != null : "Build method must not be null";
        this.builderType = builderType;
        this.resultType = resultType;
        this.builderMethodName = builderMethodName;
        this.buildMethodName = buildMethodName;
    }

    public TypeMirror getResultType() {
        return resultType;
    }

    public TypeMirror getBuilderType() {
        return builderType;
    }

    public String getBuilderMethodName() {
        return builderMethodName;
    }

    public String getBuildMethodName() {
        return buildMethodName;
    }

    public String getThawMethodName() {
        return thawMethodName;
    }

    public static BuilderInfoBuilder builder() {
        return new BuilderInfoBuilder();
    }

    public static class BuilderInfoBuilder {
        private TypeMirror builderType;
        private TypeMirror resultType;
        private String builderMethodName;
        private String buildMethodName;
        private String thawMethodName;

        public BuilderInfoBuilder builderType(TypeMirror builderType) {
            this.builderType = builderType;
            return this;
        }

        public BuilderInfoBuilder resultType(TypeMirror finalType) {
            this.resultType = finalType;
            return this;
        }

        public BuilderInfoBuilder builderMethodName(String builderMethodName) {
            this.builderMethodName = builderMethodName;
            return this;
        }

        public BuilderInfoBuilder buildMethodName(String buildMethodName) {
            this.buildMethodName = buildMethodName;
            return this;
        }

        public BuilderInfoBuilder thawMethodName(String thawMethodName) {
            this.thawMethodName = thawMethodName;
            return this;
        }

        public BuilderInfoBuilder merge(BuilderInfoBuilder... moreOptions) {
            for ( BuilderInfoBuilder builder : moreOptions ) {
                this.resultType = firstNonNull( builder.resultType, this.resultType );
                this.builderType = firstNonNull( builder.builderType, this.builderType );
                this.buildMethodName = firstNonNull( builder.buildMethodName, this.buildMethodName );
                this.builderMethodName = firstNonNull( builder.builderMethodName, this.builderMethodName );
                this.thawMethodName = firstNonNull( builder.thawMethodName, this.thawMethodName );
            }

            return this;
        }

        public BuilderInfo build() {
            return new BuilderInfo( resultType, builderType, builderMethodName, buildMethodName, thawMethodName );
        }
    }

    private static <X> X firstNonNull(X... items) {
        for ( X x : items ) {
            if ( x != null ) {
                return x;
            }
        }
        return null;
    }
}
