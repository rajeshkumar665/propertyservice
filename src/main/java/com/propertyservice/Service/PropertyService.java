package com.propertyservice.Service;

import com.propertyservice.Application;
import com.propertyservice.Controller.PropertyController;
import com.propertyservice.Dto.APIResponse;
import com.propertyservice.Dto.EmailRequest;
import com.propertyservice.Dto.PropertyDto;
import com.propertyservice.Dto.RoomsDto;
import com.propertyservice.Entity.*;
import com.propertyservice.Repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private PropertyPhotoRepository propertyPhotoRepository;

    @Autowired
    private S3Service s3Service;


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

        for(RoomsDto roomsDto: dto.getRooms()){
            Rooms rooms = new Rooms();
            rooms.setProperty(saveProperty);
            rooms.setRoomType(roomsDto.getRoomType());
            rooms.setBasePrice(roomsDto.getBasePrice());
            roomRepository.save(rooms);
        }

        List<String> filreUrls = s3Service.uploadFiles(files);
        for (String url :filreUrls){
            PropertyPhotos photos = new PropertyPhotos();
            photos.setUrl(url);
            photos.setProperty(saveProperty);
            propertyPhotoRepository.save(photos);

        }
        

        return saveProperty;

    }


    public APIResponse searchProperty(String name, LocalDate date) {
        List<Property> properties = propertyRepository.searchAvailableProperties(name, date);
        APIResponse<List<Property>> response = new APIResponse<>();
        response.setMessage("Search result");
        response.setStatus(200);
        response.setData(properties);

        return response;
    }

    public APIResponse<PropertyDto> findPropertyById(long id) {

        APIResponse<PropertyDto> response = new APIResponse<>();
        PropertyDto dto = new PropertyDto();
        Optional<Property> opProp = propertyRepository.findById(id);
        if(opProp.isPresent()){
            Property property = opProp.get();
            dto.setArea(property.getArea().getName());
            dto.setCity(property.getCity().getName());
            dto.setState(property.getState().getName());
            List<Rooms> rooms = property.getRooms();
            List<RoomsDto> roomsDto = new ArrayList<>();
            for (Rooms room:rooms){
                RoomsDto roomDto = new RoomsDto();
                BeanUtils.copyProperties(room,roomDto);
                roomsDto.add(roomDto);
            }
            dto.setRooms(roomsDto);
            BeanUtils.copyProperties(property, dto);
            response.setMessage("Matching Record");
            response.setStatus(200);
            response.setData(dto);
            return response;
        }

        return null;
    }
    public List<RoomAvailability> getTotalRoomsAvailable(long id) {
        return availabilityRepository.findByRoomId(id);

    }

    public Rooms getRoomById(long id) {
        return roomRepository.findById(id).get();
    }
}
