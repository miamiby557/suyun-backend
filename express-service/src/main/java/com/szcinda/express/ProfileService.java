package com.szcinda.express;

import com.szcinda.express.dto.PageResult;
import com.szcinda.express.dto.ProfileCreateDto;
import com.szcinda.express.dto.ProfileUpdateDto;
import com.szcinda.express.params.QueryProfileParams;
import com.szcinda.express.persistence.Profile;

import java.util.List;

public interface ProfileService {
    PageResult<Profile> query(QueryProfileParams params);
    void create(ProfileCreateDto createDto);
    void update(ProfileUpdateDto updateDto);
    void delete(String id);
    List<Profile> findByName(String name);

    Profile getById(String id);
}
