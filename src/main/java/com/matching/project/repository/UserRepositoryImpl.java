package com.matching.project.repository;

import com.matching.project.dto.enumerate.UserFilter;
import com.matching.project.dto.user.UserFilterDto;
import com.matching.project.entity.QUser;
import com.matching.project.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Slice<User> findByNoOrderByNoDescUsingQueryDsl(Long userNo, UserFilterDto userFilterDto, Pageable pageable) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if (userNo == null)
            userNo = Long.MAX_VALUE;

        if (userFilterDto.getUserFilter().name().equals(UserFilter.NAME.name()))
            builder.or(user.name.contains(userFilterDto.getContent()));
        if (userFilterDto.getUserFilter().name().equals(UserFilter.EMAIL.name()))
            builder.or(user.email.contains(userFilterDto.getContent()));

        //content를 가져오는 쿼리 fetch
        List<User> fetch = queryFactory
                .selectFrom(user)
                .where(
                        user.no.loe(userNo),
                        builder
                )
                .orderBy(user.no.desc())
                .offset(0)
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (fetch.size() > pageable.getPageSize()) {
            fetch.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(fetch, pageable, hasNext);
    }

}
