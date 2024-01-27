package com.jdc.diffverificate;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressWrapper;
import org.jetbrains.annotations.NotNull;

public class ProgressUpload extends ProgressWrapper {

    protected ProgressUpload(@NotNull ProgressIndicator original) {
        super(original);
    }
}
