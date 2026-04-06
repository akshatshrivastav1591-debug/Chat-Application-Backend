package com.Project1.ChatApplication.UserProfile.Cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary getCloudinary() {
        Map<String, Object> config = Map.of
                ("cloud_name", "dm2a2akgj"
                        , "api_key", "338385848265893"
                        , "api_secret",
                        "qQj7RBQrJkBrWcJkb5Bjx5WMtOw"
                        , "secure", true);

        return new Cloudinary(config);
    }
}
