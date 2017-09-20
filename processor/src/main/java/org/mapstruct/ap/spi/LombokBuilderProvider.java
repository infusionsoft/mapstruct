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

import static javax.lang.model.util.ElementFilter.typesIn;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link BuilderProvider} that works for lombok
 */
public class LombokBuilderProvider implements BuilderProvider {

    private static final String BUILD_METHOD_ATTRIBUTE = "buildMethodName";
    private static final String BUILDER_METHOD_ATTRIBUTE = "builderMethodName";
    private static final String BUILDER_CLASS_ATTRIBUTE = "builderClassName";

    private static final AnnotationMirrorHelper NULL_CONFIG = nullLombokConfig();

    @Override
    public BuilderInfo findBuildTarget(TypeMirror toInspect, Elements elements, Types types) {
        if ( toInspect.getKind() != TypeKind.DECLARED ) {
            return null;
        }

        final TypeElement typeElement = typeElement( toInspect, types );
        final Element enclosingElement = typeElement.getEnclosingElement();
        if ( enclosingElement != null && enclosingElement.getKind() == ElementKind.CLASS ) {
            return findBuilder( enclosingElement.asType(), elements, types );
        }
        else {
            return null;
        }
    }

    @Override
    public BuilderInfo findBuilder(TypeMirror toInspect, Elements elements, Types types) {
        if ( toInspect.getKind() != TypeKind.DECLARED ) {
            return null;
        }

        final TypeElement immutableType = typeElement( toInspect, types );
        final String immutableFQN = immutableType.getQualifiedName().toString();

        AnnotationMirrorHelper lombokConfig = NULL_CONFIG;
        for ( AnnotationMirror annotation : immutableType.getAnnotationMirrors() ) {
            final TypeElement annotationType = typeElement( annotation.getAnnotationType(), types );
            if ( annotationType.getQualifiedName().contentEquals( "lombok.Builder" ) ) {
                lombokConfig = new AnnotationMirrorHelper( elements.getElementValuesWithDefaults( annotation ) );
            }
        }

        if ( lombokConfig != NULL_CONFIG ) {
            return resolveLombokBuilder( immutableType, lombokConfig );
        }

        return null;
    }

    private BuilderInfo resolveLombokBuilder(TypeElement type, AnnotationMirrorHelper lombokConfig) {
        TypeElement builderType = findBuilderType( type, lombokConfig );
        if ( builderType != null ) {
            final BuilderInfo builderInfo = BuilderInfo.builder()
                .builderType( builderType.asType() )
                .resultType( type.asType() )
                .builderMethodName( lombokConfig.getAttribute( BUILDER_METHOD_ATTRIBUTE ) )
                .buildMethodName( lombokConfig.getAttribute( BUILD_METHOD_ATTRIBUTE ) )
                .thawMethodName( "toBuilder" )
                .build();
            return builderInfo;
        }
        return null;
    }

    private TypeElement findBuilderType(TypeElement type, AnnotationMirrorHelper lombokConfig) {
        String simpleName = lombokConfig.getAttribute( BUILDER_CLASS_ATTRIBUTE );
        if ( simpleName == null ) {
            simpleName = type.getSimpleName() + "Builder";
        }
        final String builderClassName = type.getQualifiedName() + "." + simpleName;

        for ( TypeElement typeElement : typesIn( type.getEnclosedElements() ) ) {
            if ( typeElement.getQualifiedName().contentEquals( builderClassName ) ) {
                return typeElement;
            }
        }

        return null;
    }

    private static TypeElement typeElement(TypeMirror mirror, Types types) {
        return (TypeElement) types.asElement( mirror );
    }

    public static class AnnotationMirrorHelper {
        private final Map<String, ? extends AnnotationValue> valuesWithDefaults;

        AnnotationMirrorHelper(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues) {
            Map<String, AnnotationValue> values = new HashMap<String, AnnotationValue>();

            for ( ExecutableElement key : annotationValues.keySet() ) {
                values.put( key.getSimpleName().toString(), annotationValues.get( key ) );
            }
            this.valuesWithDefaults = Collections.unmodifiableMap( values );
        }

        public String getAttribute(String key) {
            return getAttribute( key, String.class );
        }

        @SuppressWarnings( {"unchecked"})
        public <X> X getAttribute(String key, Class<X> type) {
            final AnnotationValue annotationValue = valuesWithDefaults.get( key );
            if ( annotationValue == null ) {
                return null;
            } else {
                return (X) annotationValue.getValue();
            }
        }
    }

    private static AnnotationMirrorHelper nullLombokConfig() {
        final Map<? extends ExecutableElement, ? extends AnnotationValue> empty =
            Collections.emptyMap();
        return new AnnotationMirrorHelper( empty );
    }
}
