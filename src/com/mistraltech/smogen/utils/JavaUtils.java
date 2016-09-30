package com.mistraltech.smogen.utils;

import java.util.Optional;
import java.util.stream.Stream;

public class JavaUtils {
    /**
     * Convert an Optional to a Stream of zero or one elements.
     *
     * @param opt the Optional
     * @param <T> type of element
     * @return Stream of T containing one element if the Optional is present, otherwise zero elements
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Stream<T> optionalToStream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }
}
