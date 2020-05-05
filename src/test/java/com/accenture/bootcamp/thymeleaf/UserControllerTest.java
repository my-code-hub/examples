package com.accenture.bootcamp.thymeleaf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareConcurrentModel;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UserRepository repository;

    @InjectMocks private UserController userController;

    @Test
    public void index_search() {
        String search = "foo";
        Model model = new BindingAwareConcurrentModel();

        List<User> expected = asList(
                user(1L, "john@mail.com"),
                user(2L, "jane@mail.com"));

        when(repository.findByEmailContaining(search)).thenReturn(expected);

        String actual = userController.index(search, model);

        assertThat(actual).isEqualTo("index");
        assertThat(model.getAttribute("users")).isEqualTo(expected);

        verify(repository).findByEmailContaining(search);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void index_searchWithEmptyString() {
        String search = "";
        Model model = new BindingAwareConcurrentModel();

        List<User> expected = asList(user(1L, "john@mail.com"));

        when(repository.findAll()).thenReturn(expected);

        String actual = userController.index(search, model);

        assertThat(actual).isEqualTo("index");
        assertThat(model.getAttribute("users")).isEqualTo(expected);

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void signUpForm() {
        User user = mock(User.class);

        String actual = userController.signUpForm(user);

        assertThat(actual).isEqualTo("add-user");
        verifyNoInteractions(user);
    }

    @Test
    public void updateForm() {
        Model model = new BindingAwareConcurrentModel();
        Long userId = 1L;

        User user = user(userId, "john@mail.com");

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        String actual = userController.updateForm(userId, model);

        assertThat(actual).isEqualTo("update-user");
        assertThat(model.getAttribute("user")).isEqualTo(user);

        verify(repository).findById(userId);
    }

    @Test
    public void updateForm_userNotFound() {
        Model model = new BindingAwareConcurrentModel();
        Long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.updateForm(userId, model))
                .isEqualToComparingFieldByField(new IllegalArgumentException("Invalid user Id:" + userId));

        verify(repository).findById(userId);
    }

    @Test
    public void updateUser() {
        Long userId = 1L;
        User user = user(userId, "john@mail.com");
        List<User> expectedUsers = asList(user, user(2L, "jane@mail.com"));
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(repository.findAll()).thenReturn(expectedUsers);

        String actual = userController.updateUser(userId, user, bindingResult, model);

        assertThat(actual).isEqualTo("index");

        verify(bindingResult).hasErrors();
        verify(repository).save(user);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);

        verify(model).addAttribute("users", expectedUsers);
    }

    @Test
    public void updateUser_withValidationErrors() {
        Long userId = 1L;
        User user = user(userId, "john@mail.com");
        Model model = mock(Model.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(true);

        String actual = userController.updateUser(userId, user, bindingResult, model);

        assertThat(actual).isEqualTo("update-user");

        verify(bindingResult).hasErrors();
        verifyNoInteractions(repository, model);
    }

    private User user(long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
