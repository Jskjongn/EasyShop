package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.yearup.models.Profile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MySqlProfileDaoTest extends BaseDaoTestClass{

    private MySqlProfileDao profileDao;

    @BeforeEach
    public void setup() {
        profileDao = new MySqlProfileDao(dataSource);
    }

    @Test
    public void getByUserId_shouldGet_ProfileDetailsOfUser()
    {
        // arrange
        int userId = 1;

        // act
        Profile profile = profileDao.getByUserId(userId);

        // assert
        assertEquals(1, profile.getUserId());
        assertEquals("Joe", profile.getFirstName());
        assertEquals("Joesephus", profile.getLastName());
        assertEquals("800-555-1234", profile.getPhone());
        assertEquals("joejoesephus@email.com", profile.getEmail());
        assertEquals("789 Oak Avenue", profile.getAddress());
        assertEquals("Dallas", profile.getCity());
        assertEquals("TX", profile.getState());
        assertEquals("75051", profile.getZip());
    }

    @Test
    public void getByUserId_shouldReturn_nullWhenHasNoUserId()
    {
        // arrange
        int userId = 3;

        // act
        Profile profile = profileDao.getByUserId(userId);

        // assert
        assertNull(profile);
    }

    @Test
    public void update_shouldUpdate_userProfilesDetails()
    {
        // arrange
        int userId = 2;
        Profile profile = profileDao.getByUserId(2);

        profile.setFirstName("Adam");
        profile.setLastName("Admamson");
        profile.setPhone("800-555-1212");
        profile.setEmail("aaadamson@email.com");
        profile.setAddress("123 Plae Grownd Stret");
        profile.setCity("Los Santos");
        profile.setState("CA");
        profile.setZip("90210");

        // act
        profileDao.update(userId, profile);

        // arrange
        assertEquals(2, profile.getUserId());
        assertEquals("Adam", profile.getFirstName());
        assertEquals("Admamson", profile.getLastName());
        assertEquals("800-555-1212", profile.getPhone());
        assertEquals("aaadamson@email.com", profile.getEmail());
        assertEquals("123 Plae Grownd Stret", profile.getAddress());
        assertEquals("Los Santos", profile.getCity());
        assertEquals("CA", profile.getState());
        assertEquals("90210", profile.getZip());
    }
}
