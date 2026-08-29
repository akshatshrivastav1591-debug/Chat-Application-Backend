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

        UserSecurityPojoClass FetchedDetails=new UserSecurityPojoClass();
        try {
            FetchedDetails=Repo.findBymobileno(username);
        }catch (Exception e){

        }
        if (FetchedDetails==null){

            throw new UsernameNotFoundException("user not found in database:");
        }

        return new UserPrinciple(FetchedDetails);
    }

    public UserSecurityPojoClass webSocketAuthenticationUtility(String contactNo){
        return Repo.findBymobileno(contactNo);
    }

}
