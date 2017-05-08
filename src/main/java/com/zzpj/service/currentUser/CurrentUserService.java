package com.zzpj.service.currentUser;

import com.zzpj.domain.CurrentUser;

public interface CurrentUserService {
    boolean canAccessUser(CurrentUser currentUser, Long userId);
    boolean canAccessLink(CurrentUser currentUser, String hash);
}