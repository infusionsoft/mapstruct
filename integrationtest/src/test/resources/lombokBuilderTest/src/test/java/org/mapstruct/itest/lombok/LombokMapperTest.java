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
package org.mapstruct.itest.lombok;

import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for generation of Spring-based Mapper implementations
 *
 * @author Andreas Gudian
 */
public class LombokMapperTest {

    @Test
    public void testSimpleImmutableBuilderHappyPath() {
        final org.mapstruct.itest.lombok.PersonMapper mapper = Mappers.getMapper( org.mapstruct.itest.lombok.PersonMapper.class );
        final org.mapstruct.itest.lombok.PersonDto personDto = mapper.toDto( org.mapstruct.itest.lombok.Person.builder()
            .age( 33 )
            .name( "Bob" )
            .build() );
        assertThat( personDto.getAge() ).isEqualTo( 33 );
        assertThat( personDto.getName() ).isEqualTo( "Bob" );
    }

    @Test
    public void testLombokToImmutable() {
        final org.mapstruct.itest.lombok.PersonMapper mapper = Mappers.getMapper( org.mapstruct.itest.lombok.PersonMapper.class );
        final org.mapstruct.itest.lombok.Person person = mapper.fromDto( new org.mapstruct.itest.lombok.PersonDto( "Bob", 33) );
        assertThat( person.getAge() ).isEqualTo( 33 );
        assertThat( person.getName() ).isEqualTo( "Bob" );
    }
}