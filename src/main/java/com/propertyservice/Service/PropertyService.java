package com.propertyservice.Service;

import com.propertyservice.Controller.PropertyController;
import com.propertyservice.Dto.PropertyDto;
import com.propertyservice.Entity.Area;
import com.propertyservice.Entity.City;
import com.propertyservice.Entity.Property;
import com.propertyservice.Entity.State;
import com.propertyservice.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PropertyService {


    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private RoomsRepository roomRepository;
    @Autowired
    private RoomAvailabilityRepository availabilityRepository;

    public Property addProperty(PropertyDto dto, MultipartFile[] files) {

        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());


        Property property = new Property();
        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
        property.setArea(area);
        property.setCity(city);
        property.setState(state);

        Property saveProperty = propertyRepository.save(property);

        return saveProperty;

    }
}
