/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.validation.validators;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class ChangedPasswordValidatorTest {
    private static final String PROFILE_OWNER_NAME = "owner";
    private static final long USER_ID = 1l;

	@Mock
	private UserService userService;
	@Mock
	private EncryptionService encryptionService;
	@Mock
	private ConstraintValidatorContext validatorContext;
	@Mock
	private ConstraintViolationBuilder violationBuilder;
	@Mock 
	private NodeBuilderDefinedContext nodeBuilderDefinedContext;
	private ChangedPasswordValidator validator;
	private String userCurrentPassword = "password";
	private String userNewPassword = "new_password";
	private EditUserProfileDto editUserProfileDto = new EditUserProfileDto();
	
	@BeforeMethod
	public void init() throws NotFoundException {
		initMocks(this);
		when(userService.getCurrentUser()).thenReturn(
                new JCUser(PROFILE_OWNER_NAME, "email", userCurrentPassword));
		editUserProfileDto.setUsername(PROFILE_OWNER_NAME);
        editUserProfileDto.setUserId(USER_ID);
		validator = new ChangedPasswordValidator(userService, encryptionService);
		//
		editUserProfileDto.setCurrentUserPassword(userCurrentPassword);
		editUserProfileDto.setNewUserPassword(userNewPassword);
        when(userService.get(anyLong())).thenReturn(new JCUser(PROFILE_OWNER_NAME, "email", userCurrentPassword));
	}
	
	@Test
    public void editedByModeratorShouldNotCheckCurrentPassword() throws NotFoundException {
        editUserProfileDto.setUserId(2l);

        when(userService.get(anyLong())).thenReturn(new JCUser("moderator", "email", userCurrentPassword));

        boolean isValid = validator.isValid(editUserProfileDto, validatorContext);
        
        assertTrue(isValid, "If moderator edits user's profile, we mustn't check current password.");
    }

    @Test
    public void editedNotExistingUserShouldNotCheckPassword() throws NotFoundException {
        when(userService.get(anyLong())).thenThrow(new NotFoundException());

        boolean isValid = validator.isValid(editUserProfileDto, validatorContext);

        assertTrue(isValid, "If someone try to edit not existing user's profile, we shouldn't check current password.");
    }

	@Test
	public void editedByOwnerWithNewPasswordAsNullShouldBeValid() {
		editUserProfileDto.setNewUserPassword(null);
		
		boolean isValid = validator.isValid(editUserProfileDto, validatorContext);
		
		assertTrue(isValid, "The null password is not valid.");
	}
	
	@Test
	public void editedByOwnerWithCorrectCurrentPasswordShouldBeValid() {
	    String currentUserPassword = editUserProfileDto.getCurrentUserPassword();
	    when(encryptionService.encryptPassword(currentUserPassword)).
	        thenReturn(currentUserPassword);
		
	    boolean isValid = validator.isValid(editUserProfileDto, validatorContext);
		
	    assertTrue(isValid, "The old password is correct, but the check fails.");
	}
	
	@Test
	public void editedByOwnerWithIncorrectCurrentPasswordShouldNotBeValid() {
	    String incorrectCurrentPassword = "other_password";
		editUserProfileDto.setCurrentUserPassword(incorrectCurrentPassword);
		when(encryptionService.encryptPassword(incorrectCurrentPassword)).
            thenReturn(incorrectCurrentPassword);
		when(validatorContext.buildConstraintViolationWithTemplate(null)).
				thenReturn(violationBuilder);
		when(violationBuilder.addNode(anyString())).
				thenReturn(nodeBuilderDefinedContext);
		
		boolean isValid = validator.isValid(editUserProfileDto, validatorContext);
		
		assertFalse(isValid, "The old password isn't correct, but the check passed.");
		verify(validatorContext).buildConstraintViolationWithTemplate(null);
	}
}
