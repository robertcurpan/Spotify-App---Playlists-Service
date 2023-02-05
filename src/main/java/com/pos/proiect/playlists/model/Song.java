package com.pos.proiect.playlists.model;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Song {

    private Integer songId;
    private String name;
    private List<Artist> artistsOfSong;
    private String selfLink;
}

