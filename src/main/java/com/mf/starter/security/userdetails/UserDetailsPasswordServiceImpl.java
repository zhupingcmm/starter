package com.mf.starter.security.userdetails;

import com.mf.starter.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    private final UserRepo userRepo;
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return userRepo.findByUsername(user.getUsername())
                .map(userFromDb -> (UserDetails)userRepo.save(userFromDb.withPassword(newPassword)))
                .orElse(user);
    }
}
