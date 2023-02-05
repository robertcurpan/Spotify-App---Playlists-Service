package com.pos.proiect.playlists.repository;


import com.pos.proiect.playlists.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProfilesRepository extends MongoRepository<Profile, String> {

    Profile findByUserId(Integer userId);
}
