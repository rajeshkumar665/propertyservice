package com.propertyservice.Repository;

import com.propertyservice.Entity.PropertyPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhotos,Long> {

}
