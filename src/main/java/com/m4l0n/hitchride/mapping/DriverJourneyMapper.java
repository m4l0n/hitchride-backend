package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class DriverJourneyMapper implements BaseMapper<DriverJourney, DriverJourneyDTO> {

    private final OriginDestinationMapper originDestinationMapper;
    private final UserService userService;

    public DriverJourneyMapper(OriginDestinationMapper originDestinationMapper, UserService userService) {
        this.originDestinationMapper = originDestinationMapper;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public DriverJourneyDTO mapPojoToDto(DriverJourney pojo) {
        HitchRideUser driver = userService.loadUserByUsername(pojo.getDjDriver());
        return new DriverJourneyDTO(
                pojo.getDjId(),
                driver,
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
                dto.djDriver().getUserId(),
                dto.djTimestamp(),
                originDestinationMapper.mapDtoToPojo(dto.djOriginDestination()),
                dto.djDestinationRange(),
                dto.djPrice()
        );
    }

}
