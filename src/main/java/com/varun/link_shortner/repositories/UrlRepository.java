package com.varun.link_shortner.repositories;

import com.varun.link_shortner.models.UrlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UrlRepository extends JpaRepository<UrlModel, Long> {

    UrlModel findByOriginalUrl(String url);

     Optional<UrlModel> findByShortCode(String shortCode);

     void deleteByShortCode(String shortCode);
}
