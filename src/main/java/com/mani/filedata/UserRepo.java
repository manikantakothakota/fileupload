package com.mani.filedata;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mani.filedata.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

}
