package com.example.finalProject.service;

import com.example.finalProject.exceptions.InternalErrorException;
import com.example.finalProject.object.User;
import com.example.finalProject.service.interfaces.UserService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

import static com.example.finalProject.general.Terms.USER;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public void createUser(User user) {
        Firestore db = FirestoreClient.getFirestore(FirebaseApp.getInstance("usersInfo"));

        try{
            byte[] salt = getSalt();

            user.setSaltValue(byteToString(salt));
            user.setPassword(SHA256SecurePassword(user.getPassword(), salt));

            db.collection("Persons").document(user.getEmail()).set(user);
        } catch (Exception e) {
            throw new InternalErrorException("Something went wrong");
        }
    }

    @Override
    public User validateUser(String email, String password) {
        User userFromDB = null;

        Firestore dbFirebase = FirestoreClient.getFirestore(FirebaseApp.getInstance("usersInfo"));

        DocumentReference documentReference = dbFirebase.collection(USER).document(email);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        try {
            DocumentSnapshot document = future.get();

            if (document != null && document.exists()) {
                userFromDB = document.toObject(User.class);

                if (userFromDB == null) {
                    return null;
                }

                // validate user password
                byte[] saltValueFromDB = stringToByte(userFromDB.getSaltValue());
                if (!SHA256SecurePassword(password , saltValueFromDB).equals(userFromDB.getPassword())) {
                    return null;
                } else {
                    // remove the password from response
                    userFromDB.setPassword("");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return userFromDB;
    }

    private byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String SHA256SecurePassword(String passwordToHash, byte[] salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private String byteToString(byte[] input) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(input);
    }

    private byte[] stringToByte(String input) {
        if (Base64.isBase64(input)) {
            return Base64.decodeBase64(input);
        } else {
            return Base64.encodeBase64(input.getBytes());
        }
    }
}
