package io.mkrzywanski.chat.app;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;

public class TestFluxFilterMono {

    @Test
    public void testPerson() {
        Person john = new Person("John", 22);
        StepVerifier.create(Flux.just(john.getName(), john.getAge()))
                .expectNext("John", 22)
                .expectComplete()
                .verify();
    }

    @Test
    public void testCallParse() {
        StepVerifier.create(ParsePerson.parse())
                .expectNext("John")    // how would I check for a Person "John", 22?
                .expectNext("Mary")    // how would I check for a Person "Mary", 33?
                .verifyComplete();
    }

    @Test
    public void testCallParse2() {
        StepVerifier.create(ParsePerson.parse2())
                .expectNext(new Person("John", 22))
                .expectNext(new Person("Mary", 33))
                .verifyComplete();
    }
}

class ParsePerson {
    static Flux<String> parse() {
        Flux<Person> peopleList = Flux.just(new Person("John", 22), new Person("Mary", 33));

        return peopleList
                .filter(each -> each.getAge() > 20)
                .map(Person::getName);
    }

    static Flux<Person> parse2() {
        Flux<Person> peopleList = Flux.just(new Person("John", 22), new Person("Mary", 33));
        return peopleList
                .filter(each -> each.getAge() > 20);
    }
}

class Person {

    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Person person = (Person) o;
        return getAge() == person.getAge() && Objects.equals(getName(), person.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge());
    }
}
