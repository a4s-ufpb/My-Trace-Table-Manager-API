package com.ufpb.br.apps4society.my_trace_table_manager.controller;

import com.ufpb.br.apps4society.my_trace_table_manager.dto.user.*;
import com.ufpb.br.apps4society.my_trace_table_manager.service.UserService;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserAlreadyExistsException;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.UserNotHavePermissionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@Tag(name = "User", description = "Users of Trace")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(tags = "User", summary = "Register User", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest userRequest)
            throws UserAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userRequest));
    }

    @Operation(tags = "User", summary = "Login User", responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> loginUser(@RequestBody @Valid UserLogin userLogin) {
        return ResponseEntity.ok(userService.loginUser(userLogin));
    }

    @Operation(tags = "User", summary = "Find User", responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @GetMapping(value = "/find")
    public ResponseEntity<UserResponse> findUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.findUser(token));
    }

    @Operation(tags = "User", summary = "Find All Users", responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @GetMapping(value = "/all")
    public ResponseEntity<Page<UserResponse>> findAllUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "name", defaultValue = "") String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(userService.findAllUsers(pageable, name));
    }

    @Operation(tags = "User", summary = "Remove User", responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable Long id, @RequestHeader("Authorization") String token)
            throws UserNotHavePermissionException {
        userService.removeUser(id, token);
        return ResponseEntity.noContent().build();
    }

    @Operation(tags = "User", summary = "Update User", responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdate userUpdate,
            @RequestHeader("Authorization") String token) throws UserNotHavePermissionException {
        return ResponseEntity.ok(userService.updateUser(id, userUpdate, token));
    }

    @Operation(tags = "User", summary = "Validade User Admin", responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content()),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content()),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content()),
            @ApiResponse(description = "Unauthorized", responseCode = "403", content = @Content())
    })
    @GetMapping(value = "/admin/{id}")
    public ResponseEntity<AdminResponse> validadeUserAdmin(@RequestHeader("Authorization") String token,
            @PathVariable Long id) throws UserNotHavePermissionException {
        return ResponseEntity.ok(userService.validateIfUserIsAdmin(token, id));
    }
}
