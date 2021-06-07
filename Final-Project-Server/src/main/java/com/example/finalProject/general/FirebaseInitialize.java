package com.example.finalProject.general;

import java.io.FileInputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseInitialize {
	@PostConstruct
	@SuppressWarnings("deprecation")
	public void initialize() {
		try {
			//default firebase
			FileInputStream localDB =
					new FileInputStream("./LocalDB.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(localDB)).build();

			//second firebase for international info
			FileInputStream globalDB = new FileInputStream("./GlobalDB.json");

			FirebaseOptions secondaryAppConfig = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(globalDB))
					.build();

			//third firebase for users info
			FileInputStream usersDB = new FileInputStream("./UsersDB.json");

			FirebaseOptions thirdAppConfig = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(usersDB))
					.build();

			//Initialize first firebase
			FirebaseApp.initializeApp(options);

			// Initialize second firebase
			FirebaseApp.initializeApp(secondaryAppConfig, "global");

			// Initialize third firebase
			FirebaseApp.initializeApp(thirdAppConfig, "usersInfo");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
