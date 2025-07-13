package org.swd392.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.swd392.users.entity.subscription.Subscription;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer packageId;

    @Column(name = "package_name", columnDefinition = "varchar(25)", nullable = false)
    private String packageName;

    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "packageType", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();

}
