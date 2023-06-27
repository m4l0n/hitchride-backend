package com.m4l0n.hitchride.mapping;

public interface BaseMapper<T, V> {

    public V mapPojoToDto(T pojo);

    public T mapDtoToPojo(V dto);

}
