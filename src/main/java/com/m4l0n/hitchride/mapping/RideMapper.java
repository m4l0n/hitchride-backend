package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.pojos.Ride;
import org.springframework.stereotype.Component;

@Component
public class RideMapper implements BaseMapper<Ride, RideDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final UserMapper userMapper;

    public RideMapper(OriginDestinationMapper originDestinationMapper, UserMapper userMapper) {
        this.originDestinationMapper = originDestinationMapper;
        this.userMapper = userMapper;
    }

    @Override
    public RideDTO mapPojoToDto(Ride pojo) {
       return new RideDTO(
               pojo.getRideId(),
               userMapper.mapPojoToDto(pojo.getRidePassenger()),
               originDestinationMapper.mapPojoToDto(pojo.getRideOriginDestination()),
               pojo.getRideDriverJourney()
       );
    }

    @Override
    public Ride mapDtoToPojo(RideDTO dto) {
        return new Ride(
                dto.rideId(),
                userMapper.mapDtoToPojo(dto.ridePassenger()),
                originDestinationMapper.mapDtoToPojo(dto.rideOriginDestination()),
                dto.rideDriverJourney()
        );
    }

}
