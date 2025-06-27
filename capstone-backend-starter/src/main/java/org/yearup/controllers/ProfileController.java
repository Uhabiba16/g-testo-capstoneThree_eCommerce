package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

//@RestController
//@RequestMapping("profile")
//@CrossOrigin("*")
//public class ProfileController {
//    private ProfileDao profileDao;
//    private UserDao userDao;
//
//    @Autowired
//    public ProfileController(ProfileDao profileDao, UserDao userDao) {
//        this.profileDao = profileDao;
//        this.userDao=userDao;
//    }
//
//    @GetMapping("")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Profile createProfile(@RequestBody Profile profile){
//        return null;
//    }
//
//    @GetMapping("")//{userId}
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public Profile getUserById(Profile Profile, Principal principal){
//        try{
//            String userName = principal.getName();
//            User user = userDao.getByUserName(userName);
//            int userId = user.getId();
//            return profileDao.getByUserId(userId);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
//        }
//    }
//}
