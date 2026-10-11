package com.coderound.backend.exception;

public class QuestionNotFoundException extends ResourceNotFoundException
{
    public QuestionNotFoundException(String msg){
        super(msg);
    }
}
