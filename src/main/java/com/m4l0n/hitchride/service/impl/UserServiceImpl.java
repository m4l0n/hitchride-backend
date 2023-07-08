package com.m4l0n.hitchride.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.gson.Gson;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.DriverInfoValidator;
import com.m4l0n.hitchride.service.validations.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final CollectionReference userRef;
    private final Bucket firebaseStorageBucket;
    private final UserValidator userValidator;
    private final DriverInfoValidator driverInfoValidator;
    private final AuthenticationService authenticationService;
    private final Gson gson;
    private final ObjectMapper objectMapper;


    public UserServiceImpl(Firestore firestore, Bucket firebaseStorageBucket, AuthenticationService authenticationService) {
        this.userRef = firestore.collection("users");
        this.firebaseStorageBucket = firebaseStorageBucket;
        this.authenticationService = authenticationService;
        userValidator = new UserValidator();
        gson = new Gson();
        objectMapper = new ObjectMapper();
        driverInfoValidator = new DriverInfoValidator();
    }


    @Override
    public HitchRideUser getProfile() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        HitchRideUser user = loadUserByUsername(currentLoggedInUser);

        log.info("getProfile: {}", user != null ? user.getUserId() : null);

        return user;
    }

    @Override
    public HitchRideUser createUser(HitchRideUser user) throws ExecutionException, InterruptedException {
        String errors = userValidator.validateCreateProfile(user);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }
        //Checks first if the user exists
        HitchRideUser findUser = loadUserByUsername(user.getUserId());
        if (findUser == null) {
            DriverInfo newDriverInfo = new DriverInfo();
            newDriverInfo.setDiDateJoinedTimestamp(System.currentTimeMillis());
            user.setUserDriverInfo(newDriverInfo);
            ApiFuture<WriteResult> result = userRef.document(user.getUserId())
                    .set(user);
            //Wait for the result to finish
            result.get();
            return user;
        }
        return null;
    }

    @Override
    public HitchRideUser updateUser(HitchRideUser user) throws ExecutionException, InterruptedException, JsonProcessingException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        String errors = userValidator.validateUpdateProfile(user, currentLoggedInUser);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }
        HitchRideUser findUser = loadUserByUsername(currentLoggedInUser);
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
    public HitchRideUser updateUserPoints(String userId, int points) throws ExecutionException, InterruptedException {
        HitchRideUser findUser = loadUserByUsername(authenticationService.getAuthenticatedUsername());
        if (findUser != null) {
            ApiFuture<WriteResult> result = userRef.document(findUser.getUserId())
                    .update("userPoints", FieldValue.increment(points));
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
                .update("userPhotoUrl", imageUrl);
        //Wait for the result to finish
        result.get();
        return imageUrl;
    }

    @Override
    public HitchRideUser loadUserByUsername(String username) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshot = userRef.document(username)
                .get();
        DocumentSnapshot document = documentSnapshot.get();

        if (document.exists()) {
            HitchRideUser user = mapToUserObject(document);
            log.info("loadUserByUsername: {}", user != null ? user.getUserId() : null);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public HitchRideUser updateDriverInfo(HitchRideUser user) throws ExecutionException, InterruptedException {
        String error = driverInfoValidator.validateDriverInfoCreation(user);
        if (!error.isEmpty()) {
            throw new HitchrideException(error);
        }

        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        HitchRideUser findUser = loadUserByUsername(currentLoggedInUser);

        if (findUser != null) {
            ApiFuture<WriteResult> result = userRef.document(currentLoggedInUser)
                    .update("userDriverInfo", user.getUserDriverInfo());
            //Wait for the result to finish
            result.get();
            return user;
        }
        return null;
    }

    @Override
    public Boolean updateDriverRatings(Integer newRating) throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        HitchRideUser findUser = loadUserByUsername(currentLoggedInUser);
        if (findUser != null) {
            int updatedUserRatings = (findUser.getUserDriverInfo()
                    .getDiRating() + newRating) / (findUser.getUserDriverInfo()
                    .getDiNumberOfRatings() + 1);
            ApiFuture<WriteResult> result = userRef.document(currentLoggedInUser)
                    .update("userDriverInfo.diNumberOfRatings", FieldValue.increment(1),
                            "userDriverInfo.diRating", updatedUserRatings);
            //Wait for the result to finish
            result.get();
            return true;
        }
        return false;
    }

    @Override
    public DocumentReference getUserDocumentReference(String userId) throws ExecutionException, InterruptedException {
        return userRef.document(userId);
    }

    private String uploadImageToStorage(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
        String storageFileName = "profile_pictures/" + fileName;

        BlobId blobId = BlobId.of(firebaseStorageBucket.getName(), storageFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(imageFile.getContentType())
                .build();
        firebaseStorageBucket.getStorage()
                .create(blobInfo, imageFile.getBytes());
        return "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media".formatted(firebaseStorageBucket.getName(), URLEncoder.encode(storageFileName, StandardCharsets.UTF_8));
    }


    public HitchRideUser mapToUserObject(DocumentSnapshot data) {
        String userId = (String) data.get("userId");
        String userName = (String) data.get("userName");
        String userEmail = (String) data.get("userEmail");
        String userPhoneNumber = (String) data.get("userPhoneNumber");
        String userPhotoUrl = (String) data.get("userPhotoUrl");
        Integer userPoints = ((Number) data.get("userPoints")).intValue();
        DriverInfo driverInfo = mapToDriverInfoObject((Map<String, Object>) data.get("userDriverInfo"));
        HitchRideUser user = new HitchRideUser();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        user.setUserPhoneNumber(userPhoneNumber);
        user.setUserPhotoUrl(userPhotoUrl);
        user.setUserPoints(userPoints);
        user.setUserDriverInfo(driverInfo);

        return user;
    }

    private DriverInfo mapToDriverInfoObject(Map<String, Object> data) {
        String diCarBrand = (String) data.get("diCarBrand");
        String diCarModel = (String) data.get("diCarModel");
        String diCarColor = (String) data.get("diCarColor");
        String diCarLicensePlate = (String) data.get("diCarLicensePlate");
        Long diDateJoinedTimestamp = ((Number) data.get("diDateJoinedTimestamp")).longValue();
        Long diDateCarBoughtTimestamp = ((Number) data.get("diDateCarBoughtTimestamp")).longValue();
        Boolean diIsCarSecondHand = (Boolean) data.get("diIsCarSecondHand");
        Integer diRating = ((Number) data.get("diRating")).intValue();
        Integer diNumberOfRatings = ((Number) data.get("diNumberOfRatings")).intValue();

        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setDiCarBrand(diCarBrand);
        driverInfo.setDiCarModel(diCarModel);
        driverInfo.setDiCarColor(diCarColor);
        driverInfo.setDiCarLicensePlate(diCarLicensePlate);
        driverInfo.setDiIsCarSecondHand(diIsCarSecondHand);
        driverInfo.setDiDateJoinedTimestamp(diDateJoinedTimestamp);
        driverInfo.setDiDateCarBoughtTimestamp(diDateCarBoughtTimestamp);
        driverInfo.setDiRating(diRating);
        driverInfo.setDiNumberOfRatings(diNumberOfRatings);

        return driverInfo;
    }
}
