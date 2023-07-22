package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: OriginDestinationMapper.java
// Description: Mapper for OriginDestination DTO and POJO
// Last Modified: 22 July 2023

import com.google.maps.model.PlaceDetails;
import com.m4l0n.hitchride.dto.OriginDestinationDTO;
import com.m4l0n.hitchride.pojos.LocationData;
import com.m4l0n.hitchride.pojos.OriginDestination;
import com.m4l0n.hitchride.utility.GoogleMapsApiClient;
import org.springframework.stereotype.Component;

@Component
public class OriginDestinationMapper implements BaseMapper<OriginDestination, OriginDestinationDTO> {

    private final GoogleMapsApiClient googleMapsApiClient;

    public OriginDestinationMapper(GoogleMapsApiClient googleMapsApiClient) {
        this.googleMapsApiClient = googleMapsApiClient;
    }

    @Override
    public OriginDestinationDTO mapPojoToDto(OriginDestination pojo) {
        try {
//            Map<String, String> originAddress = googleMapsApiClient.getAddressFromCoordinates(pojo.getOrigin());
//            LocationData originData = new LocationData(originAddress.get("name"), originAddress.get("address"), pojo.getOrigin());
            PlaceDetails originPlaceDetails = googleMapsApiClient.getPlaceDetailsById(pojo.getOrigin());
            LocationData originData = new LocationData(pojo.getOrigin(),
                    originPlaceDetails.name,
                    originPlaceDetails.formattedAddress);//            Map<String, String> destinationAddress = googleMapsApiClient.getAddressFromCoordinates(pojo.getDestination());
//            LocationData destinationData = new LocationData(destinationAddress.get("name"), destinationAddress.get("address"), pojo.getDestination());
            PlaceDetails destinationPlaceDetails = googleMapsApiClient.getPlaceDetailsById(pojo.getDestination());
            LocationData destinationData = new LocationData(pojo.getDestination(),
                    destinationPlaceDetails.name,
                    destinationPlaceDetails.formattedAddress);
            return new OriginDestinationDTO(originData, destinationData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OriginDestination mapDtoToPojo(OriginDestinationDTO dto) {
        return new OriginDestination(dto.origin()
                .getPlaceId(), dto.destination()
                .getPlaceId());
    }

}
