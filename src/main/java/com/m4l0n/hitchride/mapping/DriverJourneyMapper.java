package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.pojos.DriverJourney;
import org.springframework.stereotype.Component;

@Component
public class DriverJourneyMapper implements BaseMapper<DriverJourney, DriverJourneyDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final UserMapper userMapper;

    public DriverJourneyMapper(OriginDestinationMapper originDestinationMapper, UserMapper userMapper) {
        this.originDestinationMapper = originDestinationMapper;
        this.userMapper = userMapper;
    }

    @Override
    public DriverJourneyDTO mapPojoToDto(DriverJourney pojo) {
        return new DriverJourneyDTO(
                pojo.getDjId(),
                userMapper.mapPojoToDto(pojo.getDjDriver()),
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
                userMapper.mapDtoToPojo(dto.djDriver()),
                dto.djTimestamp(),
                originDestinationMapper.mapDtoToPojo(dto.djOriginDestination()),
                dto.djDestinationRange(),
                dto.djPrice()
        );
    }

}
