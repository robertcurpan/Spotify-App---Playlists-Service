package com.pos.proiect.playlists.repository;

import com.pos.proiect.playlists.model.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaylistsRepository extends MongoRepository<Playlist, String> {

    @Query("{playlistId:'?0'}")
    Playlist findPlaylistById(String playlistId);

    long count();
}
