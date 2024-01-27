package com.jdc.diffverificate.utils;

import com.intellij.serialization.MutableAccessor;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.xmlb.BeanBinding;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class XMLSerializerUtil {
    private XMLSerializerUtil() {
    }

    public static <T> void copyBean(@NotNull T from, @NotNull T to) {
        assert from.getClass().isAssignableFrom(to.getClass()) : "Beans of different classes specified: Cannot assign " +
                from.getClass() + " to " + to.getClass();
        for (MutableAccessor accessor : BeanBinding.getAccessors(from.getClass())) {
            accessor.set(to, accessor.read(from));
        }
    }

    public static <T> T createCopy(@NotNull T from) {
        try {
            @SuppressWarnings("unchecked")
            T to = (T) ReflectionUtil.newInstance(from.getClass());
            copyBean(from, to);
            return to;
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static @NotNull List<MutableAccessor> getAccessors(@NotNull Class<?> aClass) {
        return BeanBinding.getAccessors(aClass);
    }

}
