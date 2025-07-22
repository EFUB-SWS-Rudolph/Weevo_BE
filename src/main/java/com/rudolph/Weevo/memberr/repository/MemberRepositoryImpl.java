package com.rudolph.Weevo.memberr.repository;

import com.rudolph.Weevo.memberr.domain.Member;
import com.rudolph.Weevo.memberr.dto.response.MemberListResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MemberListResponse> findByMembersWithFilters(String nickname, String department, String college,
                                                           Boolean coffeeChat, Boolean donation, Boolean exchange,
                                                           Sort sort) {
        StringBuilder jpql = new StringBuilder("SELECT m FROM Member m WHERE 1=1");

        Map<String, Object> params = new HashMap<>();

        if (nickname != null) {
            jpql.append(" AND LOWER(m.nickName) LIKE LOWER(CONCAT('%', :nickname, '%'))");
            params.put("nickname", nickname);
        }
        if (department != null) {
            jpql.append(" AND m.department = :department");
            params.put("department", department);
        }
        if (college != null) {
            jpql.append(" AND m.college = :college");
            params.put("college", college);
        }
        if (coffeeChat != null) {
            jpql.append(" AND m.coffeeChat = :coffeeChat");
            params.put("coffeeChat", coffeeChat);
        }
        if (donation != null) {
            jpql.append(" AND m.donation = :donation");
            params.put("donation", donation);
        }
        if (exchange != null) {
            jpql.append(" AND m.exchange = :exchange");
            params.put("exchange", exchange);
        }

        // 정렬
        if (!sort.isEmpty()) {
            Sort.Order order = sort.iterator().next();
            jpql.append(" ORDER BY m.").append(order.getProperty())
                    .append(order.isAscending() ? " ASC" : " DESC");
        } else {
            jpql.append(" ORDER BY m.createdAt DESC");
        }

        TypedQuery<Member> query = entityManager.createQuery(jpql.toString(), Member.class);
        params.forEach(query::setParameter);

        List<Member> members = query.getResultList();

        // 엔티티 -> DTO 변환
        return members.stream()
                .map(MemberListResponse::from)
                .toList();
    }
}
