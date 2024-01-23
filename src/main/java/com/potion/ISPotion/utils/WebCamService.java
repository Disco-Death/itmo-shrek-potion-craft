package com.potion.ISPotion.utils;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.potion.ISPotion.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.GetMapping;


@Component
public class WebCamService {
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

}
