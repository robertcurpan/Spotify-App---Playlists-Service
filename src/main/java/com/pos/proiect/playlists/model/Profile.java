package com.pos.proiect.playlists.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("profiles")
@Getter @Setter @NoArgsConstructor
public class Profile {

    @Id
    private Integer userId;
    private String userName;
    private List<Playlist> playlistsOfUser;


    public Profile(String userName, List<Playlist> playlistsOfUser) {
        this.userName = userName;
        this.playlistsOfUser = playlistsOfUser;
    }
}
