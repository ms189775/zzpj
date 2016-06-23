package com.zzpj.service.link;

import com.zzpj.domain.User;
import com.zzpj.domain.Link;
import com.zzpj.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class LinkServiceImpl implements LinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkServiceImpl.class);
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
        return linkRepository.save(link);
    }
}

