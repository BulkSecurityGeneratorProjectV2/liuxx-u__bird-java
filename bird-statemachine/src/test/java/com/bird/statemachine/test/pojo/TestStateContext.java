package com.bird.statemachine.test.pojo;

import com.bird.statemachine.StateContext;
import lombok.Data;

/**
 * @author liuxx
 * @since 2021/5/11
 */
@Data
public class TestStateContext implements StateContext {

    private String id;

    private Integer age;

    private String name;

    public TestStateContext(){}

    public TestStateContext(Integer age){
        this.age = age;
    }
}
