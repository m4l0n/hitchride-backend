package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: SearchRideCriteriaMapper.java
// Description: Mapper for SearchRideCriteria DTO and POJO
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;
import org.springframework.stereotype.Component;

@Component
public class SearchRideCriteriaMapper implements BaseMapper<SearchRideCriteria, SearchRideCriteriaDTO> {

    private final OriginDestinationMapper originDestinationMapper;

    public SearchRideCriteriaMapper(OriginDestinationMapper originDestinationMapper) {
        this.originDestinationMapper = originDestinationMapper;
    }

    @Override
    public SearchRideCriteriaDTO mapPojoToDto(SearchRideCriteria pojo) {
        return new SearchRideCriteriaDTO(
                originDestinationMapper.mapPojoToDto(pojo.getSearchRideLocationCriteria()),
                pojo.getSearchRideTimestampCriteria()
        );
    }

    @Override
    public SearchRideCriteria mapDtoToPojo(SearchRideCriteriaDTO dto) {
        return new SearchRideCriteria(
                originDestinationMapper.mapDtoToPojo(dto.searchRideLocationCriteria()),
                dto.searchRideTimestampCriteria()
        );
    }

}
