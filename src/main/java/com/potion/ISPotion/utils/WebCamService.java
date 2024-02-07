package com.potion.ISPotion.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class WebCamService {
    @Autowired
    UserRepository userRepository;
    public void snapshot(String filename) throws IOException {
        try{
            Webcam webcam = Webcam.getDefault();
            webcam.open(true);
            File folder = new File("screenshots");
            if (!folder.exists()) {
                folder.mkdir();
            }
            ImageIO.write(webcam.getImage(), "PNG", new File("screenshots"+File.separator+filename+".png"));
        } catch (WebcamException ignored) {;}
    }

    @PostMapping("/snapshot")
    public void getSnapshot(@CurrentSecurityContext(expression="authentication")
                                Authentication authentication, @RequestBody byte[] bytes) throws Exception {
        // Сохранить изображение в файл
        User user = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Path path = Paths.get("snapshot"+user.getUsername()+".jpg");
        Files.write(path, bytes);
    }

    @PostMapping("/record")
    public void record(@RequestBody Blob bytes) throws Exception {
        // Сохранить запись в файл
        Path path = Paths.get("recording.wav");
        Files.write(path, bytes.getBytes(0, Long.valueOf(bytes.length()).intValue()));
    }

}
