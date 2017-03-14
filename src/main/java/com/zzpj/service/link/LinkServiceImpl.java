package com.zzpj.service.link;

import com.zzpj.domain.User;
import com.zzpj.domain.Link;
import com.zzpj.repository.LinkRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class LinkServiceImpl implements LinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkServiceImpl.class);
    private static final int DAYS = 7;
    private final LinkRepository linkRepository;

    @Autowired
    public LinkServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public Optional<Link> getLinkByHash(String hash) {
        LOGGER.debug("Getting user by email={}", hash);
        return linkRepository.findOneByHash(hash);
    }

    @Override
    public Link create(String url, String hash, User user) {
        Link link = new Link();
        link.setUrl(url);
        link.setHash(hash);
        link.setExpireDate(getDatePlusDays(DAYS));
        link.setUser(user);
        return linkRepository.save(link);
    }
    
    @Override
    public Link create(String url, String hash) {
        Link link = new Link();
        link.setUrl(url);
        link.setHash(hash);
        link.setExpireDate(getDatePlusDays(DAYS));
        return linkRepository.save(link);
    }
    
    private Date getDatePlusDays(int days) {
        LocalDateTime ldt = LocalDateTime.now().plusDays(days);
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        return Date.from(instant);
    }
}

