package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.pojos.Ride;
import org.springframework.stereotype.Component;

@Component
public class RideMapper implements BaseMapper<Ride, RideDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final DriverJourneyMapper driverJourneyMapper;

    public RideMapper(OriginDestinationMapper originDestinationMapper, DriverJourneyMapper driverJourneyMapper) {
        this.originDestinationMapper = originDestinationMapper;
        this.driverJourneyMapper = driverJourneyMapper;
    }

    @Override
    public RideDTO mapPojoToDto(Ride pojo) {
       return new RideDTO(
               pojo.getRideId(),
               pojo.getRidePassenger(),
               originDestinationMapper.mapPojoToDto(pojo.getRideOriginDestination()),
               driverJourneyMapper.mapPojoToDto(pojo.getRideDriverJourney())
       );
    }

    @Override
    public Ride mapDtoToPojo(RideDTO dto) {
        return new Ride(
                dto.rideId(),
                dto.ridePassenger(),
                originDestinationMapper.mapDtoToPojo(dto.rideOriginDestination()),
                driverJourneyMapper.mapDtoToPojo(dto.rideDriverJourney())
        );
    }

}
