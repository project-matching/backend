package com.matching.project.repository;

import com.matching.project.dto.enumerate.UserFilter;
import com.matching.project.dto.user.UserFilterDto;
import com.matching.project.entity.QUser;
import com.matching.project.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<User> findByNoOrderByNoDescUsingQueryDsl(UserFilterDto userFilterDto, Pageable pageable) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if (userFilterDto.getUserFilter().name().equals(UserFilter.NAME.name()))
            builder.or(user.name.contains(userFilterDto.getContent()));
        if (userFilterDto.getUserFilter().name().equals(UserFilter.EMAIL.name()))
            builder.or(user.email.contains(userFilterDto.getContent()));

        //content를 가져오는 쿼리 fetch
        List<User> fetch = queryFactory
                .selectFrom(user)
                .where(builder)
                .orderBy(user.no.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //count만 가져오는 쿼리
        JPQLQuery<User> count = queryFactory
                .selectFrom(user)
                .where(builder);

        return PageableExecutionUtils.getPage(fetch,pageable, count::fetchCount);
    }
}
