package com.m4l0n.hitchride.mapping;


import java.util.List;

public interface BaseMapper<E, D> {

    List<D> toDto(List<E> entityList);

    D toDto(E entity);

    E toEntity(D dto);

}
