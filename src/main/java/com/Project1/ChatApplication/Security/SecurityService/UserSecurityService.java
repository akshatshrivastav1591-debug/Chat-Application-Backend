package com.Project1.ChatApplication.Security.SecurityService;

import com.Project1.ChatApplication.Security.SecurityRepo.UserSecurityRepo;
import com.Project1.ChatApplication.Security.UserPojo.UserSecurityPojoClass;
import com.Project1.ChatApplication.Security.UserPrinciple.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {
    @Autowired
    UserSecurityRepo Repo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Start of the loadUserByUsername method:");
        UserSecurityPojoClass FetchedDetails=new UserSecurityPojoClass();
        try {
            FetchedDetails=Repo.findBymobileno(username);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        if (FetchedDetails==null){
            System.out.println("user not found:");
            throw new UsernameNotFoundException("user not found in database:");
        }
        System.out.println("loadUserByUsername method ending,method is working fine:");
        return new UserPrinciple(FetchedDetails);
    }
}
