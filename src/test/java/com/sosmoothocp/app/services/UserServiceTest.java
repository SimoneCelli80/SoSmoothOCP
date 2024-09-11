package com.sosmoothocp.app.services;

import com.sosmoothocp.app.mappers.UserMapper;
import com.sosmoothocp.app.persistence.entities.User;
import com.sosmoothocp.app.persistence.entities.factories.UserDtoFactory;
import com.sosmoothocp.app.persistence.entities.factories.UserFactory;
import com.sosmoothocp.app.persistence.repositories.UserRepository;
import com.sosmoothocp.app.rest.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Test
    void givenAValidUserUUID_whenGettingAUser_thenAUserDtoShouldBeReturned() {
        UUID userUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Optional<User> expectedUser = Optional.of(UserFactory.aUser().build());

        when(userRepository.findById(userUuid)).thenReturn(expectedUser);

        UserDto returnedUserDto = userService.getUserById(userUuid);

        assertEquals(returnedUserDto.getUuid(), expectedUser.get().getUuid());
        assertEquals(returnedUserDto.getFullName(), expectedUser.get().getFullName());
        assertEquals(returnedUserDto.getEmail(), expectedUser.get().getEmail());

        verify(userRepository, times(1)).findById(userUuid);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenAnInvalidUserUUID_whenGettingAUser_thenA404ShouldBeThrown() {
        UUID userUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Optional<User> expectedUser = Optional.empty();
        ResponseStatusException expectedException = new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with UUID %s not found. Please enter a valid UUID.", userUuid));

        when(userRepository.findById(userUuid)).thenReturn(expectedUser);

        ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, () -> userService.getUserById(userUuid));
        assertEquals(expectedException.getMessage(), thrownException.getMessage());

        verify(userRepository, times(1)).findById(userUuid);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenAListOfUsers_whenGettingAllUsers_thenAListOfUserDtosShouldBeReturned() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(UserFactory.aUser().build());
        expectedUsers.add(UserFactory.aUser().uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")).build());
        List<UserDto> expectedUserDtos = expectedUsers.stream().map(UserMapper::fromEntityToDto).collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> returnedUserDtos = userService.getAllUsers();

        assertEquals(returnedUserDtos.size(), expectedUserDtos.size());
        assertEquals(returnedUserDtos.get(0).getUuid(), expectedUserDtos.get(0).getUuid());
        assertEquals(returnedUserDtos.get(0).getFullName(), expectedUserDtos.get(0).getFullName());
        assertEquals(returnedUserDtos.get(0).getEmail(), expectedUserDtos.get(0).getEmail());
        assertEquals(returnedUserDtos.get(1).getUuid(), expectedUserDtos.get(1).getUuid());
        assertEquals(returnedUserDtos.get(1).getFullName(), expectedUserDtos.get(1).getFullName());
        assertEquals(returnedUserDtos.get(1).getEmail(), expectedUserDtos.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenNoUsers_whenGettingAllUsers_thenAnEmptyListShouldBeReturned() {
        List<User> expectedUsers = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> returnedUserDtos = userService.getAllUsers();

        assertEquals(returnedUserDtos.size(), expectedUsers.size());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}
