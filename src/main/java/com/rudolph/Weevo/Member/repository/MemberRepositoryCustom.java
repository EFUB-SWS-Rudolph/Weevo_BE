package com.rudolph.Weevo.Member.repository;

import com.rudolph.Weevo.Member.domain.Member;
import com.rudolph.Weevo.Member.dto.response.MemberListResponse;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberListResponse> findByMembersWithFilters(String nickName, String department, String college, Boolean coffeeChat, Boolean donation, Boolean exchange,
                                                      Sort sort);
}
