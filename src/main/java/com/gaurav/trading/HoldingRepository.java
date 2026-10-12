package com.gaurav.trading;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Optional<Holding> findByUserAndSymbol(AppUser user, String symbol);
    List<Holding> findByUser(AppUser user);
}
