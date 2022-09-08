package com.matching.project.repository;

import com.matching.project.dto.enumerate.UserFilter;
import com.matching.project.dto.user.UserFilterDto;
import com.matching.project.entity.QUser;
import com.matching.project.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.matching.project.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<Slice<User>> findByNoOrderByNoDescUsingQueryDsl(Long userNo, UserFilterDto userFilterDto, Pageable pageable) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        // Default Value
        if (userNo == null)
            userNo = Long.MAX_VALUE;
        if (userFilterDto.getUserFilter() == null || "".equals(userFilterDto.getUserFilter().name()))
            userFilterDto.setDefaultFilter(UserFilter.EMAIL); // 우선적으로 이메일 적용
        if (userFilterDto.getContent() == null)
            userFilterDto.setDefaultContent("");

        //content를 가져오는 쿼리 fetch
        List<User> fetch = queryFactory
                .selectFrom(user)
                .where(
                        user.no.loe(userNo),
                        filterNameEq(userFilterDto),
                        filterEmailEq(userFilterDto)
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

        return Optional.of(new SliceImpl<>(fetch, pageable, hasNext));
    }

    public BooleanExpression filterNameEq(UserFilterDto userFilterDto) {
        if (userFilterDto.getUserFilter().name().equals(UserFilter.NAME.name()))
            return user.name.contains(userFilterDto.getContent());
        else
            return null;
    }

    public BooleanExpression filterEmailEq(UserFilterDto userFilterDto) {
        if (userFilterDto.getUserFilter().name().equals(UserFilter.EMAIL.name()))
            return user.email.contains(userFilterDto.getContent());
        else
            return null;
    }

}
