package com.pos.proiect.playlists.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pos.proiect.playlists.exception.*;
import com.pos.proiect.playlists.hateoas.PlaylistModelAssembler;
import com.pos.proiect.playlists.hateoas.ProfileModelAssembler;
import com.pos.proiect.playlists.model.*;
import com.pos.proiect.playlists.service.AuthorizationService;
import com.pos.proiect.playlists.service.ProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin("http://localhost:3000")
public class ProfilesController {

    @Autowired
    private ProfilesService profilesService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ProfileModelAssembler profileModelAssembler;

    @Autowired
    private PlaylistModelAssembler playlistModelAssembler;



    @GetMapping("")
    public ResponseEntity<CollectionModel<EntityModel<Profile>>> getAllUserProfiles() {
        List<EntityModel<Profile>> userProfiles = profilesService.getAllUserProfiles().stream()
                .map(profileModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Profile>> userProfilesCollection = CollectionModel.of(userProfiles,
                linkTo(methodOn(ProfilesController.class).getAllUserProfiles()).withSelfRel()
                );

        return new ResponseEntity<>(userProfilesCollection, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EntityModel<Profile>> getUserProfile(HttpServletRequest request, @PathVariable Integer userId) throws UserNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Profile userProfile = profilesService.getUserProfile(userId);
        EntityModel<Profile> userProfileModel = profileModelAssembler.toModel(userProfile);
        return new ResponseEntity<>(userProfileModel, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<EntityModel<Profile>> createUserProfile(@PathVariable Integer userId, @RequestBody Profile profile) throws UserProfileAlreadyExistsException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        profile.setUserId(userId);
        profile.setPlaylistsOfUser(new ArrayList<>());
        Profile savedProfile = profilesService.createUserProfile(profile);
        EntityModel<Profile> savedProfileModel = profileModelAssembler.toModel(savedProfile);
        return new ResponseEntity<>(savedProfileModel, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeUserProfile(HttpServletRequest request, @PathVariable Integer userId) throws AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        profilesService.removeUserProfile(userId);
        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/playlists")
    public ResponseEntity<CollectionModel<EntityModel<Playlist>>> getAllPlaylists() {
        List<EntityModel<Playlist>> playlists = profilesService.getAllPlaylists().stream()
                .map(playlistModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Playlist>> playlistsCollection = CollectionModel.of(playlists,
                linkTo(methodOn(ProfilesController.class).getAllPlaylists()).withSelfRel()
                );

        return new ResponseEntity<>(playlistsCollection, HttpStatus.OK);
    }

    @GetMapping("/{userId}/playlists")
    public ResponseEntity<CollectionModel<EntityModel<Playlist>>> getPlaylistsForUser(HttpServletRequest request, @PathVariable Integer userId) throws UserNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        List<EntityModel<Playlist>> playlistsForUser = profilesService.getPlaylistsForUser(userId).stream()
                .map(playlist -> playlistModelAssembler.toModel(playlist, userId))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Playlist>> playlistsForUserCollection = CollectionModel.of(playlistsForUser,
                linkTo(methodOn(ProfilesController.class).getPlaylistsForUser(null, userId)).withSelfRel()
                );

        return new ResponseEntity<>(playlistsForUserCollection, HttpStatus.OK);
    }

    @GetMapping("/{userId}/playlists/{playlistId}")
    public ResponseEntity<EntityModel<Playlist>> getPlaylistForUserById(HttpServletRequest request, @PathVariable(name = "userId") Integer userId,
                                           @PathVariable(name = "playlistId") String playlistId) throws UserNotFoundException, PlaylistNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Playlist playlistForUser = profilesService.getPlaylistForUserById(userId, playlistId);
        EntityModel<Playlist> playlistForUserEntity = playlistModelAssembler.toModel(playlistForUser);
        return new ResponseEntity<>(playlistForUserEntity, HttpStatus.OK);
    }

    @PostMapping("/{userId}/playlists")
    public ResponseEntity<EntityModel<Playlist>> createPlaylistForUser(HttpServletRequest request, @PathVariable Integer userId,
                                        @RequestBody Playlist playlist) throws UserNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        // I should include a location header (that points to the url where the resource was created)
        Playlist savedPlaylist = profilesService.createPlaylistForUser(userId, playlist);
        EntityModel<Playlist> savedPlaylistEntity = playlistModelAssembler.toModel(savedPlaylist);
        URI location = URI.create("/api/profiles/" + userId.toString() + "/playlists/" + savedPlaylist.getPlaylistId());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(savedPlaylistEntity, responseHeaders, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/playlists/{playlistId}")
    public ResponseEntity<String> removePlaylistForUser(HttpServletRequest request, @PathVariable Integer userId,
                                        @PathVariable String playlistId) throws UserNotFoundException, PlaylistNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        profilesService.removePlaylistForUser(userId, playlistId);
        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{userId}/playlists/{playlistId}/songs/{songId}")
    public ResponseEntity<EntityModel<Playlist>> addSongToPlaylistForUser(HttpServletRequest request, @PathVariable Integer userId,
                                    @PathVariable String playlistId,
                                    @PathVariable Integer songId) throws JsonProcessingException, UserNotFoundException, PlaylistNotFoundException, UnknownException, SongNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Playlist updatedPlaylist = profilesService.addSongToPlaylistForUser(userId, playlistId, songId);
        EntityModel<Playlist> updatedPlaylistEntity = playlistModelAssembler.toModel(updatedPlaylist);
        return new ResponseEntity<>(updatedPlaylistEntity, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/playlists/{playlistId}/{songId}")
    public ResponseEntity<EntityModel<Playlist>> removeSongFromPlaylistForUser(HttpServletRequest request, @PathVariable Integer userId,
                                         @PathVariable String playlistId,
                                         @PathVariable Integer songId) throws UserNotFoundException, PlaylistNotFoundException, SongNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Playlist updatedPlaylist = profilesService.removeSongFromPlaylistForUser(userId, playlistId, songId);
        EntityModel<Playlist> updatedPlaylistEntity = playlistModelAssembler.toModel(updatedPlaylist);
        return new ResponseEntity<>(updatedPlaylistEntity, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<EntityModel<Profile>> changeNameOfUser(HttpServletRequest request, @PathVariable Integer userId, @RequestBody String newName) throws UserNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Profile profile = profilesService.changeNameOfUser(userId, newName);
        EntityModel<Profile> profileEntity = profileModelAssembler.toModel(profile);
        return new ResponseEntity<>(profileEntity, HttpStatus.OK);
        //TODO Numele trebuie schimbat si in proiectul Python unde se pastreaza toti userii cu informatiile lor
    }

    @PatchMapping("/{userId}/playlists/{playlistId}")
    public ResponseEntity<EntityModel<Playlist>> changePlaylistName(HttpServletRequest request, @PathVariable Integer userId,
                                     @PathVariable String playlistId,
                                     @RequestBody String newPlaylistName) throws UserNotFoundException, PlaylistNotFoundException, AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CLIENT);
        if(!hasProperRoles) throw new AccessForbiddenException();

        Playlist updatedPlaylist = profilesService.changePlaylistName(userId, playlistId, newPlaylistName);
        EntityModel<Playlist> updatedPlaylistEntity = playlistModelAssembler.toModel(updatedPlaylist);
        return new ResponseEntity<>(updatedPlaylistEntity, HttpStatus.OK);
    }

    @PostMapping("/{userId}/deleteSong")
    public ResponseEntity<String> deleteSongInAllProfilesAndPlaylists(HttpServletRequest request, @PathVariable Integer userId, @RequestBody Song song) throws AuthorizationHeaderMissingException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsExpiredException, JwsTokenCouldNotBeValidatedException, AccessForbiddenException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CONTENT_MANAGER) ||
                authorizationService.authorizeRole(userAndRoles, RolesEnum.ARTIST);
        if(!hasProperRoles) throw new AccessForbiddenException();

        profilesService.deleteSongInAllProfilesAndPlaylists(song);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/{userId}/updateSong")
    public ResponseEntity<String> updateSongInAllProfilesAndPlaylists(HttpServletRequest request, @PathVariable Integer userId, @RequestBody Song song) throws AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CONTENT_MANAGER) ||
                authorizationService.authorizeRole(userAndRoles, RolesEnum.ARTIST);
        if(!hasProperRoles) throw new AccessForbiddenException();

        profilesService.updateSongInAllProfilesAndPlaylists(song);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/{userId}/updateArtist")
    public ResponseEntity<String> updateArtistInAllProfilesAndPlaylists(HttpServletRequest request, @PathVariable Integer userId, @RequestBody Artist artist) throws AccessForbiddenException, JwsFormatNotValidException, JwsSignatureNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AuthorizationHeaderMissingException {
        String jws = authorizationService.getJwsFromRequest(request);
        UserAndRoles userAndRoles = authorizationService.validateTokenAndReturnUserAndRoles(jws, userId);
        boolean hasProperRoles = authorizationService.authorizeRole(userAndRoles, RolesEnum.CONTENT_MANAGER);
        if(!hasProperRoles) throw new AccessForbiddenException();

        profilesService.updateArtistInAllProfilesAndPlaylists(artist);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
