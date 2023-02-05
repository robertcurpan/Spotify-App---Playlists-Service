package com.pos.proiect.playlists;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class PlaylistsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaylistsApplication.class, args);
	}

}
