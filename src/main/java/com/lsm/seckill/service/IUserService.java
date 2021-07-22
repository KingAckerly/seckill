package com.lsm.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsm.seckill.entity.UserEntity;

public interface IUserService extends IService<UserEntity> {
    void batchImportUsers() throws Exception;
}
