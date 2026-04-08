package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.UserJpaEntity;
import com.beautyscheduler.domain.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserJpaEntity e) {
        return new User(e.getId(), e.getName(), e.getEmail(), e.getPasswordHash(),
                User.UserRole.valueOf(e.getRole().name()), e.getPhone(), e.getCreatedAt(), e.isActive());
    }

    public static UserJpaEntity toJpa(User u) {
        UserJpaEntity e = new UserJpaEntity();
        e.setId(u.getId());
        e.setName(u.getName());
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setRole(UserJpaEntity.RoleEnum.valueOf(u.getRole().name()));
        e.setPhone(u.getPhone());
        e.setCreatedAt(u.getCreatedAt());
        e.setActive(u.isActive());
        return e;
    }
}
