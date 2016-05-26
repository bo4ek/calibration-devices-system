package com.softserve.edu.controller;

import com.softserve.edu.entity.util.SavedFilter;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
import com.softserve.edu.service.utils.filter.SavedFilterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/globalSearch")
public class SavedFilterController {
    private static Logger logger = Logger.getLogger(SavedFilterController.class);
    @Autowired
    private SavedFilterService savedFilterService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "{locationUrl}", method = RequestMethod.GET)
    public List<Map<String, String>> getAllSavedFilters(@PathVariable("locationUrl") String locationUrl,
                                                        @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        return savedFilterService.getSavedFilters(userService.getUser(user.getUsername()), locationUrl);
    }

    @RequestMapping(value = "add/{locationUrl}", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity saveFilter(@PathVariable("locationUrl") String locationUrl,
                              @RequestBody Object filter,
                              @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        String name = ((LinkedHashMap<String, String>) filter).get("name");
        String searchParams = ((LinkedHashMap<String, String>) filter).get("filter");
        SavedFilter savedFilter=new SavedFilter(userService.getUser(user.getUsername()), locationUrl, searchParams, name);
        try {
            savedFilterService.saveFilter(savedFilter);
        } catch (Exception e) {
            logger.error("Error when adding filter", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }
    @RequestMapping(value = "delete/{locationUrl}", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity deleteFilter(@PathVariable("locationUrl") String locationUrl,
                              @RequestBody Object filter,
                              @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        HttpStatus httpStatus = HttpStatus.OK;
        String name = ((LinkedHashMap<String, String>) filter).get("name");
        try {
            savedFilterService.deleteFilter(userService.getUser(user.getUsername()), locationUrl,name);
        } catch (Exception e) {
            logger.error("Error when deleting filter", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }
    @RequestMapping(value = "update/{locationUrl}", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity updateFilter(@PathVariable("locationUrl") String locationUrl,
                              @RequestBody Object filter,
                              @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        String name = ((LinkedHashMap<String, String>) filter).get("name");
        String searchParams = ((LinkedHashMap<String, String>) filter).get("filter");
        try {
            savedFilterService.updateFilter(userService.getUser(user.getUsername()), locationUrl, searchParams, name);
        } catch (Exception e) {
            logger.error("Error when adding filter", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }
}
