package com.fabianoanticona.itanes.data.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fabianoanticona.itanes.data.local.entity.PlaceEntity;
import com.fabianoanticona.itanes.data.remote.dto.PlaceRemoteDto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PlaceMapperTest {

    @Test
    public void toEntityList_filtersNullElements() {
        // Arrange
        List<PlaceRemoteDto> remotePlaces = new ArrayList<>();
        remotePlaces.add(new PlaceRemoteDto(1, "Place 1", "S1", "D1", "A1", 0.0, 0.0, "U1", 1, "2023-01-01"));
        remotePlaces.add(null);
        remotePlaces.add(new PlaceRemoteDto(2, "Place 2", "S2", "D2", "A2", 0.0, 0.0, "U2", 2, "2023-01-01"));

        // Act
        List<PlaceEntity> entities = PlaceMapper.toEntityList(remotePlaces);

        // Assert
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals(1, entities.get(0).getId());
        assertEquals(2, entities.get(1).getId());
        
        // Verify no null elements are present
        for (PlaceEntity entity : entities) {
            assertNotNull(entity);
        }
    }

    @Test
    public void toEntityList_returnsEmptyList_whenInputIsNull() {
        // Act
        List<PlaceEntity> entities = PlaceMapper.toEntityList(null);

        // Assert
        assertNotNull(entities);
        assertTrue(entities.isEmpty());
    }
}
