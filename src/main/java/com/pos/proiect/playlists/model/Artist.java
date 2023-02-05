package com.pos.proiect.playlists.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Artist {

    private UUID artistId;
    private String artistName;
}
