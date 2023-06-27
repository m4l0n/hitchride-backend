package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.pojos.DriverJourney;
import org.springframework.stereotype.Component;

@Component
public class DriverJourneyMapper implements BaseMapper<DriverJourney, DriverJourneyDTO> {

    private final OriginDestinationMapper originDestinationMapper;

    public DriverJourneyMapper(OriginDestinationMapper originDestinationMapper) {
        this.originDestinationMapper = originDestinationMapper;
    }

    @Override
    public DriverJourneyDTO mapPojoToDto(DriverJourney pojo) {
        return new DriverJourneyDTO(
                pojo.getDjId(),
                pojo.getDjDriver(),
                pojo.getDjTimestamp(),
                originDestinationMapper.mapPojoToDto(pojo.getDjOriginDestination()),
                pojo.getDjDestinationRange(),
                pojo.getDjPrice()
        );
    }

    @Override
    public DriverJourney mapDtoToPojo(DriverJourneyDTO dto) {
        return new DriverJourney(
                dto.djId(),
                dto.djDriver(),
                dto.djTimestamp(),
                originDestinationMapper.mapDtoToPojo(dto.djOriginDestination()),
                dto.djDestinationRange(),
                dto.djPrice()
        );
    }

}
