package com.Project1.ChatApplication.WrapperClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiWrapperClass <T> {
    private T data;
    private boolean success;
    private String message;
}
