package com.szcinda.express;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryUserParams;
import com.szcinda.express.persistence.User;
import com.szcinda.express.persistence.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SnowFlakeFactory snowFlakeFactory;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    public PageResult<User> query(QueryUserParams params) {
        Specification<User> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(params.getAccount())) {
                Predicate likeAccount = criteriaBuilder.like(root.get("account").as(String.class), params.getAccount() + "%");
                predicates.add(likeAccount);
            }
            if (!StringUtils.isEmpty(params.getName())) {
                Predicate likeName = criteriaBuilder.like(root.get("name").as(String.class), params.getName() + "%");
                predicates.add(likeName);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getPageSize());
        Page<User> userPage = userRepository.findAll(specification, pageable);
        return PageResult.of(userPage.getContent(), params.getPage(), params.getPageSize(), userPage.getTotalElements());
    }

    @Override
    public UserIdentity getUserIdentity(String account, String password) {
        User user = userRepository.findFirstByAccount(account);
        Assert.notNull(user, String.format("没有此%s的用户", account));
        boolean passwordIsTrue = passwordEncoder.matches(password, user.getPassword());
        Assert.isTrue(passwordIsTrue, "密码不正确");
        UserIdentity userIdentity = new UserIdentity(user.getId(), user.getAccount(), user.getPassword(),user.getRole());
        if(RoleType.BOSS.equals(user.getRole())){
            userIdentity.setAdminPermissions();
        }
        return userIdentity;
    }

    @Override
    public void createUser(UserCreateDto createDto) {
        String password = passwordEncoder.encode(createDto.getPassword());
        User user = new User();
        BeanUtils.copyProperties(createDto, user);
        user.setId(snowFlakeFactory.nextId());
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public String generatePwd(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void updateUser(UserUpdateDto updateDto) {
        Assert.hasText(updateDto.getUserId(),"ID不能为空");
        User user = userRepository.findFirstById(updateDto.getUserId());
        user.setName(updateDto.getName());
        user.setEmail(updateDto.getEmail());
        userRepository.save(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        Assert.hasText(updatePasswordDto.getUserId(), "ID不能为空");
        User user = userRepository.getOne(updatePasswordDto.getUserId());
        String password = passwordEncoder.encode(updatePasswordDto.getNewPassword());
        Assert.isTrue(passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword()),
                "旧密码不对");
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public void delete(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Override
    public String getToken(String userId, String password) {
        return JWT.create().withAudience(userId)
                .sign(Algorithm.HMAC256(password));
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findFirstById(userId);
    }
}
