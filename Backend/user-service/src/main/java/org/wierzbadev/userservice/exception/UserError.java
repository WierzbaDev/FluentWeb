package org.wierzbadev.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserError {
    USER_NOT_FOUND("This user does not exists"),
    USER_ALREADY_EXISTS("User with this email already exists!"),
    USER_INVALID_TOKEN("Invalid refresh token"),
    USER_BLOCKED_TOKEN("Token for this user was blocked!"),
    USER_IS_BANNED("User with this email was banned and cannot get new token!"),
    USER_NOT_VERIFIED("User is not verified"),
    USER_INVALID_PASSWORD_TOKEN("Invalid token"),
    USER_INVALID_CREDENTIALS("Invalid Credentials"),
    USER_PASSWORD_NOT_MATCH("Repeat password does not match with password"),
    USER_PASSWORD_IS_WEAK("This password is too weak!"),
    USER_PASSWORD_EXPIRED("User's password token is expired"),
    USER_EMPTY_TOKEN("You forgot added token!");

    private String message;
}
