package com.pos.proiect.playlists.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.pos.proiect.playlists.exception.*;
import com.pos.proiect.playlists.model.Artist;
import com.pos.proiect.playlists.model.Playlist;
import com.pos.proiect.playlists.model.Profile;
import com.pos.proiect.playlists.model.Song;
import com.pos.proiect.playlists.repository.PlaylistsRepository;
import com.pos.proiect.playlists.repository.ProfilesRepository;
import com.pos.proiect.playlists.util.ConvertExceptionJsonToSongCollectionExceptionObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProfilesService {

    @Autowired
    private ProfilesRepository profilesRepository;

    @Autowired
    private PlaylistsRepository playlistsRepository;


    public List<Profile> getAllUserProfiles() {
        return profilesRepository.findAll();
    }

    public Profile getUserProfile(Integer userId) throws UserNotFoundException {
        Profile userProfile = profilesRepository.findByUserId(userId);
        if(userProfile == null) throw new UserNotFoundException(userId);
        return userProfile;
    }

    public Profile createUserProfile(Profile profile) throws UserProfileAlreadyExistsException {
        Profile userProfile = profilesRepository.findByUserId(profile.getUserId());
        if(userProfile != null) throw new UserProfileAlreadyExistsException(profile.getUserId());
        return profilesRepository.save(profile);
    }

    public void removeUserProfile(Integer userId) {
        Profile userProfile = profilesRepository.findByUserId(userId);
        profilesRepository.delete(userProfile);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistsRepository.findAll();
    }

    public List<Playlist> getPlaylistsForUser(Integer userId) throws UserNotFoundException {
        Profile userProfile = getUserProfile(userId);

        List<Playlist> playlistsOfUser = userProfile.getPlaylistsOfUser();
        return (playlistsOfUser == null) ? new ArrayList<>() : playlistsOfUser;
    }

    public Playlist getPlaylistForUserById(Integer userId, String playlistId) throws UserNotFoundException, PlaylistNotFoundException {
        Profile userProfile = getUserProfile(userId);

        for (Playlist playlist : userProfile.getPlaylistsOfUser()) {
            if(Objects.equals(playlist.getPlaylistId(), playlistId)) {
                return playlist;
            }
        }

        throw new PlaylistNotFoundException(playlistId);
    }

    public Playlist createPlaylistForUser(Integer userId, Playlist playlist) throws UserNotFoundException {
        Profile userProfile = getUserProfile(userId);

        playlist.setPlaylistSongs(new ArrayList<>());
        Playlist createdPlaylist = playlistsRepository.save(playlist);
        userProfile.getPlaylistsOfUser().add(playlist);
        profilesRepository.save(userProfile);

        return createdPlaylist;
    }

    public void removePlaylistForUser(Integer userId, String playlistId) throws UserNotFoundException, PlaylistNotFoundException {
        Profile userProfile = getUserProfile(userId);

        Playlist playlistToDelete = getPlaylistForUserById(userId, playlistId);
        playlistsRepository.delete(playlistToDelete);

        for(Playlist playlist : userProfile.getPlaylistsOfUser()) {
            if(Objects.equals(playlist.getPlaylistId(), playlistId)) {
                userProfile.getPlaylistsOfUser().remove(playlist);
                profilesRepository.save(userProfile);
                break;
            }
        }
    }

    public Playlist addSongToPlaylistForUser(Integer userId, String playlistId, Integer songId) throws JsonProcessingException, UserNotFoundException, PlaylistNotFoundException, SongNotFoundException, UnknownException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://localhost:8080/api/songcollection/songs/forPlaylists/" + songId;

        Song song = null;
        try {
            song = restTemplate.getForObject(uri, Song.class);
        } catch (HttpClientErrorException ex) {
            ErrorObject exceptionObject = ConvertExceptionJsonToSongCollectionExceptionObjectUtil.getExceptionObjectFromHttpClientExceptionJson(ex);
            int statusCode = exceptionObject.getStatus().value();
            String message = exceptionObject.getMessage();

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                throw new SongNotFoundException(songId);
            } else {
                throw new UnknownException();
            }
        }

        Profile userProfile = getUserProfile(userId);
        Playlist playlistToAddSongTo = getPlaylistForUserById(userId, playlistId);
        Playlist savedPlaylist = null;

        for(Playlist playlist : userProfile.getPlaylistsOfUser()) {
            if(Objects.equals(playlist.getPlaylistId(), playlistToAddSongTo.getPlaylistId())) {
                playlist.getPlaylistSongs().add(song);
                savedPlaylist = playlistsRepository.save(playlist);
                profilesRepository.save(userProfile);
            }
        }

        return savedPlaylist;
    }

    public Playlist removeSongFromPlaylistForUser(Integer userId, String playlistId, Integer songId) throws UserNotFoundException, PlaylistNotFoundException, SongNotFoundException {
        Profile userProfile = getUserProfile(userId);
        Playlist playlistToRemoveSongFrom = getPlaylistForUserById(userId, playlistId);

        for(Playlist playlist : userProfile.getPlaylistsOfUser()) {
            if(Objects.equals(playlist.getPlaylistId(), playlistToRemoveSongFrom.getPlaylistId())) {
                for(int index = 0; index < playlist.getPlaylistSongs().size(); ++index) {
                    if(songId != null && Objects.equals(playlist.getPlaylistSongs().get(index).getSongId(), songId)) {
                        playlist.getPlaylistSongs().remove(index);
                        Playlist savedPlaylist = playlistsRepository.save(playlist);
                        profilesRepository.save(userProfile);
                        return savedPlaylist;
                    }
                }
            }
        }

        throw new SongNotFoundException(songId);
    }

    public Profile changeNameOfUser(Integer userId, String newName) throws UserNotFoundException {
        Profile profile = getUserProfile(userId);
        profile.setUserName(newName);
        return profilesRepository.save(profile);
    }

    public Playlist changePlaylistName(Integer userId, String playlistId, String newPlaylistName) throws UserNotFoundException, PlaylistNotFoundException {
        Profile profile = getUserProfile(userId);

        for(Playlist playlist : profile.getPlaylistsOfUser()) {
            if(Objects.equals(playlist.getPlaylistId(), playlistId)) {
                playlist.setPlaylistName(newPlaylistName);
                Playlist updatedPlaylist = playlistsRepository.save(playlist);
                profilesRepository.save(profile);
                return updatedPlaylist;
            }
        }

        throw new PlaylistNotFoundException(playlistId);
    }

    public void deleteSongInAllProfilesAndPlaylists(Song song) {
        List<Profile> allProfiles = getAllUserProfiles();
        boolean playlistModifiedInCurrentProfile;
        boolean songDeletedInPlaylist;

        for(Profile profile : allProfiles) {
            playlistModifiedInCurrentProfile = false;

            for(Playlist playlist: profile.getPlaylistsOfUser()) {
                songDeletedInPlaylist = deleteSongInPlaylist(playlist, song);
                if(songDeletedInPlaylist) {
                    playlistsRepository.save(playlist);
                    playlistModifiedInCurrentProfile = true;
                }
            }

            if(playlistModifiedInCurrentProfile) {
                profilesRepository.save(profile);
            }
        }
    }

    public void updateSongInAllProfilesAndPlaylists(Song song) {
        List<Profile> allProfiles = getAllUserProfiles();
        boolean playlistModifiedInCurrentProfile;
        boolean songModifiedInPlaylist;

        for(Profile profile : allProfiles) {
            playlistModifiedInCurrentProfile = false;

            for(Playlist playlist: profile.getPlaylistsOfUser()) {
                songModifiedInPlaylist = updateSongInPlaylist(playlist, song);
                if(songModifiedInPlaylist) {
                    playlistsRepository.save(playlist);
                    playlistModifiedInCurrentProfile = true;
                }
            }

            if(playlistModifiedInCurrentProfile) {
                profilesRepository.save(profile);
            }
        }
    }

    public void updateArtistInAllProfilesAndPlaylists(Artist artist) {
        List<Profile> allProfiles = getAllUserProfiles();
        boolean playlistModifiedInCurrentProfile;
        boolean artistModifiedInPlaylist;

        for(Profile profile : allProfiles) {
            playlistModifiedInCurrentProfile = false;

            for(Playlist playlist : profile.getPlaylistsOfUser()) {
                artistModifiedInPlaylist = updateArtistInPlaylist(playlist, artist);
                if(artistModifiedInPlaylist) {
                    playlistsRepository.save(playlist);
                    playlistModifiedInCurrentProfile = true;
                }
            }

            if(playlistModifiedInCurrentProfile) {
                profilesRepository.save(profile);
            }
        }
    }

    // ********************************************************************************* //

    public boolean deleteSongInPlaylist(Playlist playlist, Song song) {
        for(int index = 0; index < playlist.getPlaylistSongs().size(); ++index) {
            if(Objects.equals(playlist.getPlaylistSongs().get(index).getSongId(), song.getSongId())) {
                playlist.getPlaylistSongs().remove(index);
                return true;
            }
        }

        return false;
    }

    public boolean updateSongInPlaylist(Playlist playlist, Song song) {
        for(int index = 0; index < playlist.getPlaylistSongs().size(); ++index) {
            if(Objects.equals(playlist.getPlaylistSongs().get(index).getSongId(), song.getSongId())) {
                playlist.getPlaylistSongs().set(index, song);
                return true;
            }
        }

        return false;
    }

    public boolean updateArtistInPlaylist(Playlist playlist, Artist artist) {
        boolean artistWasUpdated = false;

        for(Song song : playlist.getPlaylistSongs()) {
            for(int index = 0; index < song.getArtistsOfSong().size(); ++index) {
                if(song.getArtistsOfSong().get(index).getArtistId().equals(artist.getArtistId())) {
                    song.getArtistsOfSong().set(index, artist);
                    artistWasUpdated = true;
                    break;
                }
            }
        }

        return artistWasUpdated;
    }
}
