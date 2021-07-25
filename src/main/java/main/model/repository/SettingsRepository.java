package main.model.repository;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<GlobalSettings, Integer> {

    @Query(value = "SELECT * FROM global_settings WHERE code = ?1", nativeQuery = true)
    GlobalSettings getSetting(String code);

}
