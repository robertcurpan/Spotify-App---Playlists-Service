package com.pos.proiect.playlists.hateoas;

import com.pos.proiect.playlists.controller.ProfilesController;
import com.pos.proiect.playlists.exception.*;
import com.pos.proiect.playlists.model.Profile;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProfileModelAssembler implements RepresentationModelAssembler<Profile, EntityModel<Profile>> {

    @Override
    public EntityModel<Profile> toModel(Profile profile) {
        try {
            EntityModel<Profile> profileEntity = EntityModel.of(profile,
                linkTo(methodOn(ProfilesController.class).getUserProfile(null, profile.getUserId())).withSelfRel(),
                linkTo(methodOn(ProfilesController.class).getAllUserProfiles()).withRel("profiles"),
                linkTo(methodOn(ProfilesController.class).removeUserProfile(null, profile.getUserId())).withRel("removeProfile").withType("DELETE"),
                linkTo(methodOn(ProfilesController.class).createPlaylistForUser(null, profile.getUserId(), null)).withRel("createPlaylistForUser").withType("POST")
            );

            if(!profile.getPlaylistsOfUser().isEmpty()) {
                profileEntity.add(
                        linkTo(methodOn(ProfilesController.class).removePlaylistForUser(null, profile.getUserId(), null))
                                .withRel("removePlaylistForUser").withType("DELETE")
                );
            }

            profileEntity.add(
                    linkTo(methodOn(ProfilesController.class).changeNameOfUser(null, profile.getUserId(), null))
                            .withRel("changeNameOfUser").withType("PATCH")
            );

            return profileEntity;
        } catch (UserNotFoundException | PlaylistNotFoundException | AccessForbiddenException |
                 JwsFormatNotValidException | JwsSignatureNotValidException | JwsTokenCouldNotBeValidatedException |
                 JwsExpiredException | AuthorizationHeaderMissingException e) {
            throw new RuntimeException(e);
        }

    }
}
