package com.varun.link_shortner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlModel, Long> {

    UrlModel findByOriginalUrl(String url);

     UrlModel findByShortCode(String shortCode);
}
