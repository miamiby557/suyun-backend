package com.szcinda.express;

import com.szcinda.express.dto.PageResult;
import com.szcinda.express.dto.ProfileCreateDto;
import com.szcinda.express.dto.ProfileUpdateDto;
import com.szcinda.express.params.QueryProfileParams;
import com.szcinda.express.persistence.Profile;
import com.szcinda.express.persistence.ProfileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final SnowFlakeFactory snowFlakeFactory;
    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    @Override
    public PageResult<Profile> query(QueryProfileParams params) {
        Specification<Profile> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(params.getName())) {
                Predicate likeAccount = criteriaBuilder.like(root.get("name").as(String.class), params.getName().trim() + "%");
                predicates.add(likeAccount);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getPageSize());
        Page<Profile> userPage = profileRepository.findAll(specification, pageable);
        return PageResult.of(userPage.getContent(), params.getPage(), params.getPageSize(), userPage.getTotalElements());
    }

    @Override
    public void create(ProfileCreateDto createDto) {
//        Profile profile = profileRepository.findByName(createDto.getName());
//        Assert.isTrue(profile == null, String.format("已经存在相同名称[%s]的档案", createDto.getName()));
        Profile pro = new Profile();
        BeanUtils.copyProperties(createDto, pro);
        pro.setId(snowFlakeFactory.nextId());
        profileRepository.save(pro);
    }

    @Override
    public void update(ProfileUpdateDto updateDto) {
        Profile profile = profileRepository.findFirstById(updateDto.getId());
        BeanUtils.copyProperties(updateDto, profile, "id", "name");
        profileRepository.save(profile);
    }

    @Override
    public void delete(String id) {
        Profile profile = profileRepository.findFirstById(id);
        profileRepository.delete(profile);
    }

    @Override
    public List<Profile> findByName(String name) {
        return profileRepository.findByName(name.trim());
    }

    @Override
    public Profile getById(String id) {
        return profileRepository.findFirstById(id);
    }
}
