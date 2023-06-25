package com.m4l0n.hitchride.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.LocalDateTypeAdapter;
import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.UserValidator;
import com.m4l0n.hitchride.utility.LocalDateConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final CollectionReference userRef;
    private final Bucket firebaseStorageBucket;
    private final UserValidator userValidator;
    private final AuthenticationService authenticationService;
    private final Gson gson;
    private final ObjectMapper objectMapper;


    public UserServiceImpl(Firestore firestore, Bucket firebaseStorageBucket, AuthenticationService authenticationService) {
        this.userRef = firestore.collection("users");
        this.firebaseStorageBucket = firebaseStorageBucket;
        this.authenticationService = authenticationService;
        userValidator = new UserValidator();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();
        objectMapper = new ObjectMapper();
    }


    @Override
    public User getProfile() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        User user = loadUserByUsername(currentLoggedInUser);

        log.info("getProfile: {}", user != null ? user.getUserId() : null);

        return user;
    }

    @Override
    public User createUser(User user) throws ExecutionException, InterruptedException {
        //If user does not have an ID, set the ID to the Firebase Authentication ID
        if (user.getUserId() == null) {
            user.setUserId(authenticationService.getAuthenticatedUsername());
        }
        //Checks first if the user exists
        User findUser = loadUserByUsername(user.getUserId());
        if (findUser == null) {
            DriverInfo userDriverInfo = user.getUserDriverInfo();
            userDriverInfo.setDiDateJoined(LocalDate.now());
            //Convert to map before saving to Firestore, due to LocalDate serialization issues
            Map<String, Object> userMap = gson.fromJson(gson.toJson(user), new TypeToken<>() {
            });
            ApiFuture<WriteResult> result = userRef.document(user.getUserId())
                    .set(userMap);
            //Wait for the result to finish
            result.get();
            return user;
        }
        return null;
    }

    @Override
    public User updateUser(User user) throws ExecutionException, InterruptedException, JsonProcessingException {
        User findUser = loadUserByUsername(authenticationService.getAuthenticatedUsername());
        if (findUser != null) {
            //Filter null values from user object and convert it to a map
            final Map<String, Object> userMap = objectMapper.readValue(gson.toJson(user),
                    new TypeReference<>() {
                    });
            ApiFuture<WriteResult> result = userRef.document(findUser.getUserId())
                    .update(userMap);
            //Wait for the result to finish
            result.get();
            return user;
        }
        return null;
    }

    @Override
    public User updateUserPoints(int points) throws ExecutionException, InterruptedException {
        User findUser = loadUserByUsername(authenticationService.getAuthenticatedUsername());
        if (findUser != null) {
            ApiFuture<WriteResult> result = userRef.document(findUser.getUserId())
                    .update("userPoints", FieldValue.increment(-points));
            //Wait for the result to finish
            result.get();
            return findUser;
        }
        return null;
    }

    @Override
    public String updateUserProfilePicture(MultipartFile imageFile) throws IOException, ExecutionException, InterruptedException {
        String error = userValidator.validateProfilePicture(imageFile);
        if (!error.isEmpty()) {
            throw new HitchrideException(error);
        }
        //Upload image to Firebase Storage
        String imageUrl = uploadImageToStorage(imageFile);
        //Update user profile picture in Firestore
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        ApiFuture<WriteResult> result = userRef.document(currentLoggedInUser)
                .update("userProfilePicture", imageUrl);
        //Wait for the result to finish
        result.get();
        return imageUrl;
    }

    @Override
    public User loadUserByUsername(String username) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshot = userRef.document(username)
                .get();
        DocumentSnapshot document = documentSnapshot.get();

        if (document.exists()) {
            User user = mapToUserObject(document);
            log.info("loadUserByUsername: {}", user != null ? user.getUserId() : null);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, GeoPoint> getUserSavedLocations() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        User user = loadUserByUsername(currentLoggedInUser);

        if (user != null) {
            log.info("getUserSavedLocations: {}", Optional.ofNullable(user.getUserSavedLocations()));
            return user.getUserSavedLocations();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, GeoPoint> saveUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        User user = loadUserByUsername(currentLoggedInUser);

        if (user != null) {

            String errors = userValidator.validateSaveUserLocation(user, location);
            if (!errors.isEmpty()) {
                throw new HitchrideException(errors);
            }

            Map<String, GeoPoint> userSavedLocations = user.getUserSavedLocations();
            userSavedLocations.putAll(location);
            ApiFuture<WriteResult> result = userRef.document(user.getUserId()).update("userSavedLocations", userSavedLocations);
            //Wait for the result to finish
            result.get();
            return location;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, GeoPoint> deleteUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public Map<String, GeoPoint> updateUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException {
        return null;
    }

    private String uploadImageToStorage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
        String storageFileName = "images/" + fileName;

        BlobId blobId = BlobId.of(firebaseStorageBucket.getName(), storageFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(imageFile.getContentType())
                .build();
        firebaseStorageBucket.getStorage()
                .create(blobInfo, imageFile.getBytes());
        return "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media".formatted(firebaseStorageBucket.getName(), URLEncoder.encode(storageFileName, StandardCharsets.UTF_8));
    }

    public User mapToUserObject(DocumentSnapshot data) {
        String userId = (String) data.get("userId");
        String userName = (String) data.get("userName");
        String userEmail = (String) data.get("userEmail");
        String userPhoneNumber = (String) data.get("userPhoneNumber");
        String userPhotoUrl = (String) data.get("userPhotoUrl");
        Integer userPoints = ((Number) data.get("userPoints")).intValue();
        Map<String, GeoPoint> userSavedLocations = (Map<String, GeoPoint>) data.get("userSavedLocations");
        DriverInfo driverInfo = mapToDriverInfoObject((Map<String, Object>) data.get("userDriverInfo"));
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        user.setUserPhoneNumber(userPhoneNumber);
        user.setUserPhotoUrl(userPhotoUrl);
        user.setUserPoints(userPoints);
        user.setUserSavedLocations(userSavedLocations);
        user.setUserDriverInfo(driverInfo);

        return user;
    }

    private DriverInfo mapToDriverInfoObject(Map<String, Object> data) {
        String diCarBrand = (String) data.get("diCarBrand");
        String diCarModel = (String) data.get("diCarModel");
        String diCarColor = (String) data.get("diCarColor");
        String diCarLicensePlate = (String) data.get("diCarLicensePlate");
        Timestamp diDateJoinedTimestamp = (Timestamp) data.get("diDateJoined");
        Timestamp diDateCarBoughtTimestamp = (Timestamp) data.get("diDateCarBought");
        Boolean diIsCarSecondHand = (Boolean) data.get("diIsCarSecondHand");
        Integer diRating = ((Number) data.get("diRating")).intValue();

        LocalDate diDateJoined = LocalDateConverter.fromFirestoreTimestamp(diDateJoinedTimestamp != null ? diDateJoinedTimestamp : Timestamp.now());
        LocalDate diDateCarBought = LocalDateConverter.fromFirestoreTimestamp(diDateCarBoughtTimestamp != null ? diDateCarBoughtTimestamp : Timestamp.now());

        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setDiCarBrand(diCarBrand);
        driverInfo.setDiCarModel(diCarModel);
        driverInfo.setDiCarColor(diCarColor);
        driverInfo.setDiCarLicensePlate(diCarLicensePlate);
        driverInfo.setDiDateJoined(diDateJoined);
        driverInfo.setDiDateCarBought(diDateCarBought);
        driverInfo.setDiIsCarSecondHand(diIsCarSecondHand);
        driverInfo.setDiRating(diRating);

        return driverInfo;
    }
}
