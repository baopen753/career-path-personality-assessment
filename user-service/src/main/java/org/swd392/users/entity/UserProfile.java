package org.swd392.users.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profile")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private LocalDate birthDay;
    private String phoneNumber;
    private String address;
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "school", length = 30)
    private String school;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 20)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id") // tạo cột user_id trong bảng user_profile
    @JsonBackReference
    private User user;

}