package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.Classes.StorageRecord;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class TrackingController {
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("permissionsParts")
    public Set<String> headerPermission(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication) {
        return AuthUtils.getHeaderPermissions(userRepository, authentication);
    }

    @ModelAttribute("recordDuration")
    public Integer recordDuration(@CurrentSecurityContext(expression="authentication")
                                  Authentication authentication) {
        User user = AuthUtils.getUserByAuthentication(userRepository, authentication);
        if (user.isTracking()) {
            return user.getRecordDuration();
        }
        return -1;
    }

    @GetMapping("/loading")
    public  String loading(@CurrentSecurityContext(expression="authentication")
                        Authentication authentication,  Model model) throws IOException {
        model.addAttribute("title", "Зельевар");
        model.addAttribute("authentication", authentication);
        return "track-front";
    }

    @GetMapping("/track")
    public String testsDisplay(@CurrentSecurityContext(expression="authentication")
                               Authentication authentication, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Iterable<User> users = userRepository.findAll();

        File dir = new File("video");
        File[] arrFiles = dir.listFiles();
        List<String> lst = new ArrayList<>();
        if (arrFiles != null) {
            for (File file: arrFiles
                 ) {
                lst.add(file.getPath());
            }
        }

        model.addAttribute("users", users );
        model.addAttribute("files", lst);
        model.addAttribute("title", "Система отслеживания");

        return "track";
    }

    @PostMapping(value = "/track/update-settings")
    public String updateSettings(@CurrentSecurityContext(expression="authentication")
                              Authentication authentication, HttpServletRequest  request, @RequestBody String body) throws Exception {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        JSONObject data = new JSONObject(body);
        User user = userRepository.findById(Long.parseLong(data.getString("id"))).orElseThrow();
        if (data.getString("name").contains("status")) {
            user.setTracking(data.getBoolean("value"));
        }
        if (data.getString("name").contains("duration")) {
            user.setRecordDuration(data.getInt("value"));
        }
        userRepository.save(user);

        return "home";
    }

    @PostMapping("/track/delete/{name}")
    public String potionDelete(@CurrentSecurityContext(expression="authentication")
                               Authentication authentication, @PathVariable(value = "name") String filename) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";
        File file = new File("video" + File.separator + filename);
        file.delete();
        return "redirect:/track";
    }
}
