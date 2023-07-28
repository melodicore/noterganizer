package me.datafox.noterganizer.client.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A response contains either a value of an arbitrary type, or a
 * status {@link String} denoting that an error occurred during a request.
 *
 * @author datafox
 */

@EqualsAndHashCode
@ToString
public class Response<T> {
    public static <T> Response<T> of(T value) {
        return new Response<>(value);
    }

    public static <T> Response<T> error(String status) {
        return new Response<>(status);
    }

    public static <T> Response<T> error(Throwable exception) {
        String status = exception.getMessage();
        if(status == null) status = exception.getClass().getName();
        return new Response<>(status);
    }

    public static <T> Response<T> error(int httpStatus) {
        String status = "Http status code: " + httpStatus;
        return new Response<>(status);
    }

    private final T value;

    private final String status;

    public Response(T value) {
        this.value = value;
        if(value == null) {
            status = "nullResponse";
        } else {
            status = null;
        }
    }

    public Response(String status) {
        value = null;
        this.status = status;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isError() {
        return value == null;
    }

    public T get() {
        if(isPresent()) return value;
        throw new NoSuchElementException("No value present");
    }

    public String status() {
        return status;
    }

    public void ifPresent(Consumer<? super T> action) {
        if(isPresent()) action.accept(value);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Consumer<String> statusAction) {
        if(isPresent()) {
            action.accept(value);
        } else {
            statusAction.accept(status);
        }
    }

    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    public T orElseThrow() {
        return get();
    }

    public <X extends Throwable> T orElseThrow(Function<String, ? extends X> exceptionSupplier) throws X {
        if(isPresent()) return value;
        throw exceptionSupplier.apply(status);
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }
}
