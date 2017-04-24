package com.zzpj.service.link;

import com.zzpj.domain.User;
import com.zzpj.domain.Link;

import java.util.Collection;
import java.util.Optional;

public interface LinkService {
    Optional<Link> getLinkByHash(String hash);
    Link create(String url, String hash, User user);
    Link create(String url, String hash);
    void renew(Link link);
}
