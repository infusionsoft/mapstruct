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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * A service provider interface that is used to detect types that require a builder for mapping, and also to detect
 * builder classes themselves.  This interface could support automatic detection for projects like Lombok, Immutables,
 * AutoValue, etc.
 */
public interface BuilderProvider {

    /**
     * Looks for a builder for the {@code typeMirror} type.  If found, information about the configuration is
     * returned.  Otherwise, null.
     * @param typeMirror The potentially immutable type we are
     * @param elements
     * @param types
     * @return
     */
    BuilderMapping findBuilder(TypeMirror typeMirror, Elements elements, Types types);

    BuilderMapping findBuildTarget(TypeMirror typeMirror, Elements elements, Types types);
}
