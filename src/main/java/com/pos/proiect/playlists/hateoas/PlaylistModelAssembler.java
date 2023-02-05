package com.pos.proiect.playlists.hateoas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pos.proiect.playlists.controller.ProfilesController;
import com.pos.proiect.playlists.exception.*;
import com.pos.proiect.playlists.model.Playlist;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class PlaylistModelAssembler implements RepresentationModelAssembler<Playlist, EntityModel<Playlist>> {

    @Override
    public EntityModel<Playlist> toModel(Playlist playlist) {
        try {
            EntityModel<Playlist> playlistEntity = EntityModel.of(playlist,
                    linkTo(methodOn(ProfilesController.class).getPlaylistForUserById(null, null, playlist.getPlaylistId())).withSelfRel(),
                    linkTo(methodOn(ProfilesController.class).getPlaylistsForUser(null, null)).withRel("playlistsOfUser"),
                    linkTo(methodOn(ProfilesController.class).removePlaylistForUser(null, null, playlist.getPlaylistId())).withRel("removePlaylist").withType("DELETE"),
                    linkTo(methodOn(ProfilesController.class).addSongToPlaylistForUser(null, null, playlist.getPlaylistId(), null)).withRel("addSongToPlaylist").withType("POST")
            );

            if(!playlist.getPlaylistSongs().isEmpty()) {
                playlistEntity.add(
                        linkTo(methodOn(ProfilesController.class).removeSongFromPlaylistForUser(null, null, playlist.getPlaylistId(), null))
                                .withRel("removeSongFromPlaylist").withType("POST")
                );
            }

            return playlistEntity;
        } catch (UserNotFoundException | PlaylistNotFoundException | UnknownException | JsonProcessingException |
                 SongNotFoundException | AccessForbiddenException | JwsFormatNotValidException |
                 JwsSignatureNotValidException | JwsTokenCouldNotBeValidatedException | JwsExpiredException |
                 AuthorizationHeaderMissingException e) {
            throw new RuntimeException(e);
        }

    }

    public EntityModel<Playlist> toModel(Playlist playlist, Integer userId) {
        try {
            EntityModel<Playlist> playlistEntity = EntityModel.of(playlist,
                    linkTo(methodOn(ProfilesController.class).getPlaylistForUserById(null, userId, playlist.getPlaylistId())).withSelfRel(),
                    linkTo(methodOn(ProfilesController.class).getPlaylistsForUser(null, userId)).withRel("playlistsOfUser"),
                    linkTo(methodOn(ProfilesController.class).removePlaylistForUser(null, userId, playlist.getPlaylistId())).withRel("removePlaylist").withType("DELETE"),
                    linkTo(methodOn(ProfilesController.class).addSongToPlaylistForUser(null, userId, playlist.getPlaylistId(), null)).withRel("addSongToPlaylist").withType("POST")
            );

            if(!playlist.getPlaylistSongs().isEmpty()) {
                playlistEntity.add(
                        linkTo(methodOn(ProfilesController.class).removeSongFromPlaylistForUser(null, userId, playlist.getPlaylistId(), null))
                                .withRel("removeSongFromPlaylist").withType("POST")
                );
            }

            return playlistEntity;
        } catch (UserNotFoundException | PlaylistNotFoundException | UnknownException | JsonProcessingException |
                 SongNotFoundException | AccessForbiddenException | JwsFormatNotValidException |
                 JwsSignatureNotValidException | JwsTokenCouldNotBeValidatedException | JwsExpiredException |
                 AuthorizationHeaderMissingException e) {
            throw new RuntimeException(e);
        }

    }

}
