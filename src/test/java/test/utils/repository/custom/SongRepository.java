/*
 * Copyright 2020 Hazelcast Inc.
 *
 * Licensed under the Hazelcast Community License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://hazelcast.com/hazelcast-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package test.utils.repository.custom;

import test.utils.domain.Song;

import java.util.List;

/**
 * <P>Extend the {@link MyTitleRepository} for song titles,
 * inheriting some methods application to the base class {@link MyTitle}
 * and adding some methods specific to {@link Song} objects.
 * </P>
 *
 * @author Neil Stevenson
 */
public interface SongRepository
        extends MyTitleRepository<Song, String> {

    /**
     * <p>
     * This could be generic behaviour, but make specific
     * to {@link SongRepository} for tests.
     * </P>
     *
     * @param text A year
     * @return Previous years
     */
    public List<Song> findByIdLessThan(String text);

}
