package com.claim.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


public final class PageUtils {
    private PageUtils() {}


    public static Pageable enforceAllowedSizes(Pageable pageable, int... allowed) {
        Set<Integer> set = new HashSet<>();
        Arrays.stream(allowed).forEach(set::add);
        int size = set.contains(pageable.getPageSize()) ? pageable.getPageSize() : 20;
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}