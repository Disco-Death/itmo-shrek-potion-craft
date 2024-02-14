package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import org.springframework.stereotype.Controller;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Controller
public class WebCamService {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.parse("10MB"));
        factory.setMaxRequestSize(DataSize.parse("10MB"));

        return factory.createMultipartConfig();
    }
    @Autowired
    UserRepository userRepository;
    @PostMapping(value = "/record")
    public String getSnapshot(@CurrentSecurityContext(expression="authentication")
                                Authentication authentication, HttpServletRequest  request) throws Exception {
        try {
            User user = AuthUtils.getUserByAuthentication(userRepository, authentication);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-hh-mm");
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Set set = multipartRequest.getFileMap().entrySet();
            Iterator i = set.iterator();
            while(i.hasNext()) {
                Map.Entry me = (Map.Entry)i.next();
                String fileName = user.getUsername() + "-" + java.time.LocalDateTime.now().format(formatter) + ".webm";
                MultipartFile multipartFile = (MultipartFile)me.getValue();
                writeToDisk(fileName, multipartFile);
            }
        }
        catch (Exception ignored) {

        }
        return "home";
    }
    public void writeToDisk(String filename, MultipartFile multipartFile)
    {
        try
        {
            File folder = new File("video");
            if (!folder.exists()) {
                folder.mkdir();
            }

            String fullFileName = "video"+File.separator+ filename;
            FileOutputStream fos = new FileOutputStream(fullFileName);
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
