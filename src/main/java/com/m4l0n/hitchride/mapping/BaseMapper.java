package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: BaseMapper.java
// Description: Interface for mapping between DTO and POJO
// Last Modified: 22 July 2023

public interface BaseMapper<T, V> {

    public V mapPojoToDto(T pojo);

    public T mapDtoToPojo(V dto);

}
