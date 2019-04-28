package com.bird.service.zero.impl;

import com.bird.service.common.service.AbstractService;
import com.bird.service.zero.UserLoginAttemptService;
import com.bird.service.zero.mapper.UserLoginAttemptMapper;
import com.bird.service.zero.model.UserLoginAttempt;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "zero_user_login_attempt")
@org.apache.dubbo.config.annotation.Service
public class UserLoginAttemptServiceImpl extends AbstractService<UserLoginAttemptMapper,UserLoginAttempt> implements UserLoginAttemptService {
}
