package com.lsm.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsm.seckill.entity.UserEntity;
import com.lsm.seckill.mapper.UserMapper;
import com.lsm.seckill.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Transactional
    @Override
    public void batchImportUsers() throws Exception {
        UserEntity userEntity;
        List<UserEntity> userEntities = new ArrayList<>();
        String Url = "/Users/xiaosi/develop/user.txt";
        String line = System.getProperty("line.separator");
        File fout = new File(Url);
        FileWriter out = new FileWriter(fout);
        for (int i = 1; i <= 10000; i++) {
            userEntity = new UserEntity();
            userEntity.setName("user-" + i);
            userEntity.setMobile("17727921123");
            userEntities.add(userEntity);
            out.write(String.valueOf(i));
            out.write(line);
        }
        batchImportUsers(userEntities);
        out.flush();
        out.close();
    }

    private void batchImportUsers(List<UserEntity> userEntities) {
        //每次执行的数量
        int post = 3000;
        int fromIndex = 0;
        //初始截取索引=执行数量
        int toIndex = post;
        //源集合长度
        int size = userEntities.size();
        //需要分批执行的总次数
        int total = size % post == 0 ? size / post : size / post + 1;
        //承载分批次执行的集合
        List newList;
        //当前执行次数
        int index = 1;
        while (true) {
            if (index < total) {
                newList = userEntities.subList(fromIndex, toIndex);
                saveBatch(newList);
                fromIndex = toIndex;
                toIndex += post;
                index++;
            }
            if (index == total) {
                newList = userEntities.subList(fromIndex, size);
                saveBatch(newList);
                break;
            }
        }
    }
}
