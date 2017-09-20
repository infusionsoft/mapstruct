package org.mapstruct.ap.internal.model;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.ap.internal.model.source.SourceMethod;
import org.mapstruct.ap.spi.BuilderInfo;
import org.mapstruct.ap.spi.BuilderProvider;

/**
 * Factory for discovering builders.
 */
public class BuilderFactory {

    private static final String METHOD_TEMPLATE = "<SOURCE>.%s";
    private static final String VAR_NAME = "mappedBuilder";
    private final Elements elements;
    private final Types types;
    private final List<BuilderProvider> builderProviders;

    private final Map<TypeMirror, BuilderInfo> buildsInfo;
    private final Map<TypeMirror, BuilderInfo> builtByInfo;

    public BuilderFactory(Elements elements, Types types, List<BuilderProvider> builderProviders) {

        this.elements = elements;
        this.types = types;
        this.builderProviders = builderProviders;
        this.buildsInfo = new HashMap<TypeMirror, BuilderInfo>();
        this.builtByInfo = new HashMap<TypeMirror, BuilderInfo>();
    }

    /**
     * Inspects a list of {@link SourceMethod}s and returns {@link BuilderInfo} for any mapping targets.
     *
     * @param mirror The mirror to discover a builder for
     * @return Information about a builder for this type.
     */
    public BuilderInfo findBuilderForType(TypeMirror mirror) {

        if ( builtByInfo.containsKey( mirror ) ) {
            return builtByInfo.get( mirror );
        }

        for ( BuilderProvider provider : builderProviders ) {

            final BuilderInfo info = provider.findBuilder( mirror, elements, types );
            if ( info != null ) {
                builtByInfo.put( mirror, info );
                return info;
            }
        }

        builtByInfo.put( mirror, null );
        return null;
    }

    public BuilderInfo findBuildTargetForType(TypeMirror mirror) {
        if ( buildsInfo.containsKey( mirror ) ) {
            return buildsInfo.get( mirror );
        }
        for ( BuilderProvider provider : builderProviders ) {

            final BuilderInfo info = provider.findBuildTarget( mirror, elements, types );
            if ( info != null ) {
                buildsInfo.put( mirror, info );

                return info;
            }
        }
        buildsInfo.put( mirror, null );
        return null;
    }
}
