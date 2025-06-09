package org.swp392.users.dto;

import org.swp392.users.entity.AccountType;
import org.swp392.users.entity.Gender;

import java.time.LocalDate;

public class UserProfileDTO {
    private Long userId;
    private Long profileId;
    private String fullName;
    private LocalDate birthDay;
    private String phoneNumber;
    private String address;
    private String imageUrl;
    private String school;
    private AccountType accountType;
    private Gender gender;

    // Constructors
    public UserProfileDTO() {}

    public UserProfileDTO(Long userId, Long profileId, String fullName, LocalDate birthDay, String phoneNumber,
                          String address, String imageUrl, String school, AccountType accountType, Gender gender) {
        this.userId = userId;
        this.profileId = profileId;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.imageUrl = imageUrl;
        this.school = school;
        this.accountType = accountType;
        this.gender = gender;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
