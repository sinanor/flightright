package com.flightright.codechallange.mapper;


public interface Mapper<DTO, ENT> {
    ENT convertToEntity(DTO dto);

    DTO convertToDTO(ENT ent);
}
