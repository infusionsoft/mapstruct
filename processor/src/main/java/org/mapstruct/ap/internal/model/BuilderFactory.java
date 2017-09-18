package org.mapstruct.ap.internal.model;

import org.mapstruct.ap.internal.model.BuilderMappingModel;
import org.mapstruct.ap.internal.model.MethodReference;
import org.mapstruct.ap.internal.model.common.Parameter;
import org.mapstruct.ap.internal.model.common.ParameterBinding;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common.TypeFactory;
import org.mapstruct.ap.internal.model.source.Method;
import org.mapstruct.ap.internal.model.source.SourceMethod;
import org.mapstruct.ap.spi.BuilderInfo;
import org.mapstruct.ap.spi.BuilderProvider;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for working with builder mappings and models
 */
public class BuilderFactory {

    private static final String METHOD_TEMPLATE = "<SOURCE>.%s";
    private static final String VAR_NAME = "mappedBuilder";
    private final TypeFactory typeFactory;
    private final Elements elements;
    private final Types types;
    private final List<BuilderProvider> builderProviders;

    public BuilderFactory(TypeFactory typeFactory,
                          Elements elements,
                          Types types,
                          List<BuilderProvider> builderProviders) {

        this.typeFactory = typeFactory;
        this.elements = elements;
        this.types = types;
        this.builderProviders = builderProviders;
    }

    /**
     * Inspects a list of {@link SourceMethod}s and returns {@link BuilderInfo} for any mapping targets.
     *
     * @param methods The methods to inspect
     * @return A map of types that require a builder.
     */
    public Map<Type, BuilderMappingModel> getBuildersFromMethods(List<SourceMethod> methods) {
        Map<Type, BuilderMappingModel> builderInfos = new HashMap<Type, BuilderMappingModel>();
        for ( SourceMethod method : methods ) {
            for ( BuilderProvider provider : builderProviders ) {
                final TypeMirror resultType = method.getResultType().getTypeMirror();
                final BuilderInfo builderInfo = provider.findBuilder( resultType, elements, types );
                if ( builderInfo != null ) {
                    builderInfos.put( method.getResultType(), convertToModel( builderInfo ) );
                    break;
                }
            }
        }

        return builderInfos;
    }

    private BuilderMappingModel convertToModel(BuilderInfo builderInfo) {
        final Type builderType = typeFactory.getType( builderInfo.getBuilderType(), true );
        final Type finalType = typeFactory.getType( builderInfo.getFinalType() );

        return new BuilderMappingModel( finalType, builderType,
            getBuilderMethodReference( builderInfo, builderType, finalType ),
            getBuildMethodReference( builderInfo, builderType, finalType ),
            getThawMethodReference( builderInfo, builderType, finalType ) );

    }

//    public BeanMappingMethod getBuilderMappingMethod(MappingBuilderContext context, BuilderMappingModel model,
//        ForgedMethod forgedMappingMethod) {
//
//        return new BeanMappingMethod.Builder()
//            .mappingContext( context )
//            .forgedMethod( forgedMappingMethod )
//            .factoryMethod( model.getBuilderCreationMethod() )
//            .build();
//    }

    private MethodReference getBuilderMethodReference(BuilderInfo builderInfo, Type builderType, Type finalType) {
        //ericm:fix Fragile, could this come from a type other than finalType?
        return MethodReference.forLocalMethod(
            sourceMethod( finalType, builderType, builderInfo.getBuilderCreationMethod() ),
            Collections.<ParameterBinding>emptyList()
        );
    }

    private Method sourceMethod(Type definingType, Type returnType, ExecutableElement method) {
        return new SourceMethod.Builder()
            .setExecutable( method )
            .setTypeUtils( types )
            .setTypeFactory( typeFactory )
            .setReturnType( returnType )
            .setParameters( java.util.Collections.<Parameter>emptyList() )
            .setExceptionTypes( java.util.Collections.<Type>emptyList() )
            .setDefininingType( definingType )
            .build();
    }

    private MethodReference getBuildMethodReference(BuilderInfo builderInfo, Type builderType, Type finalType) {
        return MethodReference.forParameterProvidedMethod(
            sourceMethod( builderType, finalType, builderInfo.getFinalizeMethod() ),
            new Parameter( VAR_NAME, builderType )
        );
    }

    private MethodReference getThawMethodReference(BuilderInfo builderInfo, Type builderType, Type finalType) {
        if ( builderInfo.getThawMethod() != null ) {
            return MethodReference.forLocalMethod(
                sourceMethod( finalType, builderType, builderInfo.getThawMethod() ),
                ParameterBinding.empty()
            );
        }
        return null;
    }


}
