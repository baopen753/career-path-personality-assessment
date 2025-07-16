package org.swd392.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisteredEvent {
    private String userEmail;
    private String userName;
    private String username;
    private String accountType;
    private String registrationDate;
    private String loginLink;

    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public String getUsername() { return username; }
    public String getAccountType() { return accountType; }
    public String getRegistrationDate() { return registrationDate; }
    public String getLoginLink() { return loginLink; }
} 