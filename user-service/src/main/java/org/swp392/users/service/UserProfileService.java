package org.swp392.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swp392.users.entity.User;
import org.swp392.users.entity.UserProfile;
import org.swp392.users.repository.UserProfileRepository;
import org.swp392.users.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return profileRepository.findById(userId);
    }

    public UserProfile createOrUpdateProfile(Long userId, UserProfile profileDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserProfile> optionalProfile = profileRepository.findById(userId);

        UserProfile profile = optionalProfile.orElse(new UserProfile());
        profile.setUser(user);
        profile.setFullName(profileDetails.getFullName());
        profile.setBirthDay(profileDetails.getBirthDay());
        profile.setGender(profileDetails.getGender());

        return profileRepository.save(profile);
    }

    public boolean deleteProfile(Long userId) {
        return profileRepository.findById(userId).map(profile -> {
            profileRepository.delete(profile);
            return true;
        }).orElse(false);
    }

}
