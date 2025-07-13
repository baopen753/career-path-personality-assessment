package org.swd392.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swd392.users.entity.Package;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Integer> {
    Package findPackageByPackageName(String packageName);

}
