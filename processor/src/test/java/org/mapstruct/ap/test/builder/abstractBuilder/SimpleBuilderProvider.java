package org.mapstruct.ap.test.builder.abstractBuilder;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mapstruct.ap.spi.BuilderInfo;
import org.mapstruct.ap.spi.BuilderProvider;

public class SimpleBuilderProvider implements BuilderProvider {

    @Override
    public BuilderInfo findBuilder(TypeMirror typeMirror, Elements elements, Types types) {
        final TypeElement buildTarget = (TypeElement) types.asElement( typeMirror );
        final TypeElement builder = elements.getTypeElement( buildTarget.getQualifiedName() + ".Builder" );
        if ( builder != null ) {
            return BuilderInfo.builder()
                .resultType( buildTarget.asType() )
                .builderType( builder.asType() )
                .builderMethodName( "builder" )
                .buildMethodName( "build" )
                .build();
        }

        return null;
    }

    @Override
    public BuilderInfo findBuildTarget(TypeMirror typeMirror, Elements elements, Types types) {
        final TypeElement builder = (TypeElement) types.asElement( typeMirror );
        if ( builder.getSimpleName().contentEquals( "Builder" ) ) {
            return BuilderInfo.builder()
                .resultType( builder.getEnclosingElement().asType() )
                .builderType( builder.asType() )
                .builderMethodName( "builder" )
                .buildMethodName( "build" )
                .build();
        }

        return null;
    }
}
