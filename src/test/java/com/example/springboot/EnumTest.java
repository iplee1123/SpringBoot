package com.example.springboot;

import com.example.springboot.batch.test.Gender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumTest {

    @Test
    void enumTest() {
        Gender gender = Gender.MALE;
        assertThat(gender).isEqualTo(Gender.MALE);
        System.out.println(gender.getDeclaringClass());
        System.out.println(gender.getClass());
    }

}
